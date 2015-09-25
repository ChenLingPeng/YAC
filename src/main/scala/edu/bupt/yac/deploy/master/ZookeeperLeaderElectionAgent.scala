package edu.bupt.yac.deploy.master

import edu.bupt.yac.commons.YacConf
import edu.bupt.yac.utils.YacCuratorUtils
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.leader.{LeaderLatch, LeaderLatchListener}
import org.apache.log4j.Logger

/**
 * Created by chenlingpeng on 15/9/24.
 */
class ZookeeperLeaderElectionAgent(val masterActor: LeaderElectable, conf: YacConf)
  extends LeaderLatchListener with LeaderElectionAgent {
  private val log = Logger.getLogger(classOf[ZookeeperLeaderElectionAgent])

  private val WORKING_DIR = conf.get("yac.deploy.zookeeper.dir", "/yac") + "/leader_election"

  private var zookeeper: CuratorFramework = _
  private var leaderLatch: LeaderLatch = _
  private var status = LeadershipStatus.NOT_LEADER

  private def start() = {
    log.info("Starting Zookeeper LeaderElection agent")
    zookeeper = YacCuratorUtils.newClient(conf)
    leaderLatch = new LeaderLatch(zookeeper, WORKING_DIR)
    leaderLatch.addListener(this)
    leaderLatch.start()
  }

  start()

  override def isLeader: Unit = {
    synchronized {
      if(!leaderLatch.hasLeadership) {
        return
      }
      log.info("Gain leadership")
      updateLeadershipStatus(true)
    }
  }

  override def notLeader(): Unit = {
    synchronized {
      if(leaderLatch.hasLeadership) {
        return
      }
      log.info("Revoke leadership")
      updateLeadershipStatus(false)
    }
  }

  private def updateLeadershipStatus(isLeader: Boolean) = {
    if(isLeader && status == LeadershipStatus.NOT_LEADER) {
      status = LeadershipStatus.LEADER
      masterActor.electedLeader()
    } else if(!isLeader && status == LeadershipStatus.LEADER) {
      status = LeadershipStatus.NOT_LEADER
      masterActor.revokedLeadership()
    }
  }

  private object LeadershipStatus extends Enumeration {
    type LeadershipStatus = Value
    val LEADER, NOT_LEADER = Value
  }
}
