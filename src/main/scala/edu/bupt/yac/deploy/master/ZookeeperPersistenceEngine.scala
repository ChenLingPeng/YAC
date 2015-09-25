package edu.bupt.yac.deploy.master

import java.nio.ByteBuffer

import edu.bupt.yac.commons.YacConf
import edu.bupt.yac.serializer.Serializer
import edu.bupt.yac.utils.YacCuratorUtils
import org.apache.log4j.Logger
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException.NodeExistsException

import scala.reflect.ClassTag
import scala.collection.JavaConverters._

/**
 * Created by chenlingpeng on 15/9/22.
 */
class ZookeeperPersistenceEngine(val conf: YacConf, val serializer: Serializer) extends PersistedEngine{
  private val log = Logger.getLogger(classOf[ZookeeperPersistenceEngine])

  private val WORKING_DIR = conf.get("yac.deploy.zookeeper.dir", "/yac") + "/master_status"

  private val zookeeper = YacCuratorUtils.newClient(conf)

  YacCuratorUtils.mkdir(zookeeper, WORKING_DIR)

  override def persist(name: String, obj: Object): Unit = {
    log.info(s"Persist $name")
    serializeToPath(WORKING_DIR + "/" + name, obj)
  }

  override def unpersist(name: String): Unit = {
    log.info(s"Delete persisted $name")
    zookeeper.delete().forPath(WORKING_DIR + "/" + name)
  }

  override def close(): Unit = {
    zookeeper.close()
  }

  private def serializeToPath(path: String, obj: AnyRef) = {
    val serialized = serializer.newInstance().serialize(obj)
    val bytes = new Array[Byte](serialized.remaining())
    serialized.get(bytes)
    try {
      zookeeper.create().withMode(CreateMode.PERSISTENT).forPath(path, bytes)
    } catch {
      case e: NodeExistsException =>
        log.error(s"Node $path existed! Can't persist")
    }
  }

  private def deserializeFromPath[T](path: String)(implicit m: ClassTag[T]): Option[T] = {
    val data = zookeeper.getData.forPath(WORKING_DIR + "/" + path)
    try {
      Some(serializer.newInstance().deserialize[T](ByteBuffer.wrap(data)))
    } catch {
      case e: Exception =>
        log.warn(s"Exception while reading persisted data in $path, deleting", e)
        zookeeper.delete().forPath(WORKING_DIR + "/" + path)
        None
    }
  }

  override def readSeq[T: ClassTag](prefix: String): Seq[T] = {
    zookeeper.getChildren.forPath(WORKING_DIR).asScala
      .filter(_.startsWith(prefix)).map(deserializeFromPath[T]).flatten
  }

  override def read[T: ClassTag](path: String): Option[T] = {
    deserializeFromPath[T](path)
  }
}
