package edu.bupt.yac.server

import java.util.concurrent.Executors

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import akka.io.IO
import edu.bupt.yac.commons.{RevokedLeadership, ElectedLeader}
import edu.bupt.yac.config.YacConfig
import edu.bupt.yac.deploy.master.LeaderElectable
import edu.bupt.yac.server.actor.{JobScanActor, JobControllerActor}
import org.apache.log4j.Logger
import spray.can.Http

import scala.concurrent.ExecutionContext

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 15:20.
 */
object YacSystem {
  val log = Logger.getLogger(classOf[YacSystem])
  lazy implicit val system = ActorSystem(YacConfig.actorSystemName,YacConfig.config.getConfig("yac.server.SeverSys"))
  lazy implicit val exec = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
  def props() = Props[YacSystem]

  var controllerActor: ActorRef = _
  var httpService: ActorRef = _
  var jobScanActor: ActorRef = _

  def apply() = system

  def main (args: Array[String]) {
//    YacSystem()
    system.actorOf(props(), "YACSystem")
  }
}

class YacSystem extends Actor with LeaderElectable {
  import YacSystem.log
  import context.system
  val httpServerActor = {
    val service = context.actorOf(HttpServer.props(), "yac-server")
    IO(Http) ! Http.Bind(service, "localhost", port = 1113)
    log.info("http server started")
    service
  }

  val jobControllerActor = JobControllerActor()
  val jobScanActor = JobScanActor()

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = super.preStart()

  override def receive: Receive = ???

  override def electedLeader(): Unit = {
    jobScanActor ! ElectedLeader
    jobControllerActor ! ElectedLeader
  }

  override def revokedLeadership(): Unit = {
    jobScanActor ! RevokedLeadership
    jobControllerActor ! RevokedLeadership
  }
}
