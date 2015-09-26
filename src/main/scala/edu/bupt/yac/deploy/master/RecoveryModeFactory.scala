package edu.bupt.yac.deploy.master

import edu.bupt.yac.commons.YacConf
import edu.bupt.yac.serializer.Serializer
import org.apache.log4j.Logger

/**
 * Created by chenlingpeng on 15/9/21.
 */
abstract class RecoveryModeFactory(conf: YacConf, serializer: Serializer) {
  def createPersistenceEngine(): PersistedEngine

  def createLeaderElectionAgent(master: LeaderElectable): LeaderElectionAgent
}

class FileSystemRecoveryModeFactory(conf: YacConf, serializer: Serializer) extends RecoveryModeFactory(conf, serializer) {
  val log = Logger.getLogger(classOf[FileSystemRecoveryModeFactory])

  val RECOVERY_DIR = conf.get("yac.deploy.master.recoveryDirectory", ".")

  override def createPersistenceEngine(): PersistedEngine = {
    log.info("Persisting recovery state to directory " + RECOVERY_DIR)
    new FileSystemPersistenceEngine(RECOVERY_DIR, serializer)
  }

  override def createLeaderElectionAgent(master: LeaderElectable): LeaderElectionAgent = {
    log.info("Only one master, elect myself as leader")
    new SingleMasterLeaderAgent(master)
  }
}

class ZookeeperRecoveryModeFactory(conf: YacConf, serializer: Serializer) extends RecoveryModeFactory(conf, serializer) {
  val log = Logger.getLogger(classOf[ZookeeperRecoveryModeFactory])

  override def createPersistenceEngine(): PersistedEngine = {
    log.info("Persisting recovery state to zookeeper")
    new ZookeeperPersistenceEngine(conf, serializer)
  }

  override def createLeaderElectionAgent(master: LeaderElectable): LeaderElectionAgent = ???
}