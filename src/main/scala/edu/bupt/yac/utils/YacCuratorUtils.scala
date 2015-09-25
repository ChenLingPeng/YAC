package edu.bupt.yac.utils

import edu.bupt.yac.commons.YacConf
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.log4j.Logger
import org.apache.zookeeper.KeeperException

/**
 * Created by chenlingpeng on 15/9/22.
 */
object YacCuratorUtils {
  val log = Logger.getLogger(YacCuratorUtils.getClass)

  private val ZK_URL_CONF_KEY = "yac.deploy.zookeeper.url"
  private val ZK_CONNECTION_TIMEOUT_MILLIS = 15000
  private val ZK_SESSION_TIMEOUT_MILLIS = 60000
  private val RETRY_WAIT_MILLIS = 5000
  private val MAX_RECONNECT_ATTEMPTS = 3

  def newClient(conf: YacConf) = {
    val ZK_URL = conf.get(ZK_URL_CONF_KEY, "angel0:2181,angel0:2182,angel0:2183")
    val zookeeper = CuratorFrameworkFactory.newClient(ZK_URL,
      ZK_SESSION_TIMEOUT_MILLIS, ZK_CONNECTION_TIMEOUT_MILLIS,
      new ExponentialBackoffRetry(RETRY_WAIT_MILLIS, MAX_RECONNECT_ATTEMPTS))
    zookeeper.start()
    zookeeper
  }

  def mkdir(zk: CuratorFramework, path: String): Unit = {
    if(zk.checkExists().forPath(path) == null) {
      try {
        zk.create().creatingParentsIfNeeded().forPath(path)
      } catch {
        case nodeExist: KeeperException.NodeExistsException =>
          // do nothing
        case e: Exception =>
          throw e
      }
    }
  }

  def deleteRecursive(zk: CuratorFramework, path: String) = {
    import scala.collection.JavaConverters._
    if(zk.checkExists().forPath(path) != null) {
      zk.getChildren.forPath(path).asScala.foreach{
        child =>
          zk.delete().forPath(path + "/" + child)
      }
      zk.delete().forPath(path)
    }
  }
}
