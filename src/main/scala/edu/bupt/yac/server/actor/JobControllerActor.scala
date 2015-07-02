package edu.bupt.yac.server.actor

import akka.actor.{Props, Actor}
import edu.bupt.yac.commons.{JobDelete, JobAdd}
import edu.bupt.yac.server.YacSystem
import org.apache.log4j.Logger

/**
 * User: chenlingpeng 
 * Date: 2015/7/2 11:09.
 */

/**
 * 负责Job的创建，删除，定时启动等
 */
class JobControllerActor extends Actor{
  val log = Logger.getLogger(this.getClass)
  log.info("JobControllerActor start")
  override def receive: Receive = {
    case JobAdd(jobDir) =>
    case JobDelete(jobName) =>
  }
}

object JobControllerActor {
  private def props = Props[JobControllerActor]

  lazy val jobControllerActor = YacSystem().actorOf(props)

  def apply() = jobControllerActor
}
