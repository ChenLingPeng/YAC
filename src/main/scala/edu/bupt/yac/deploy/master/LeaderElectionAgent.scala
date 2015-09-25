package edu.bupt.yac.deploy.master

/**
 * Created by chenlingpeng on 15/9/21.
 */
trait LeaderElectionAgent {
  val masterActor: LeaderElectable
  def stop() {}
}

trait LeaderElectable {
  def electedLeader()
  def revokedLeadership()
}

class SingleMasterLeaderAgent(val masterActor: LeaderElectable)
  extends LeaderElectionAgent {
  masterActor.electedLeader()
}
