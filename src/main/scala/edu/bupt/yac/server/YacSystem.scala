package edu.bupt.yac.server

//import java.util.concurrent.Executors

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import akka.io.IO
import edu.bupt.yac.commons.{YacConf, RevokedLeadership, ElectedLeader}
import edu.bupt.yac.config.YacConfig
import edu.bupt.yac.deploy.master._
import edu.bupt.yac.serializer.KryoSerializer
import edu.bupt.yac.server.actor.{JobScanActor, JobControllerActor}
import org.apache.log4j.Logger
import spray.can.Http

//import scala.concurrent.ExecutionContext

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 15:20.
 */
object YacSystem {
  val log = Logger.getLogger(classOf[YacSystem])
  lazy implicit val system = ActorSystem(YacConfig.actorSystemName,YacConfig.config.getConfig("yac.server.SeverSys"))
//  lazy implicit val exec = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
  def props() = Props[YacSystem]
  sys.addShutdownHook(system.shutdown())
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

  val conf = new YacConf
  val RECOVERY_MODE = conf.get("yac.deploy.recoveryMode", "NONE")
  var persistenceEngine: PersistedEngine = _
  var leaderElectionAgent: LeaderElectionAgent = _

  val httpServerActor = {
    val service = context.actorOf(HttpServer.props(), "yac-server")
    IO(Http) ! Http.Bind(service, conf.get("yac.server.ip", "localhost"), port = conf.get("yac.server.port", "1113").toInt)
    log.info("http server started")
    service
  }

  val jobControllerActor = JobControllerActor()
  val jobScanActor = JobScanActor()

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    val (persistenceEngine_, leaderElectionAgent_) = RECOVERY_MODE.toUpperCase match {
      case "ZOOKEEPER" =>
        log.info("Recovery mode is zookeeper")
        val factory = new ZookeeperRecoveryModeFactory(conf, new KryoSerializer(conf))
        (factory.createPersistenceEngine(), factory.createLeaderElectionAgent(this))
      case "FILESYSTEM" =>
        log.info("Recovery mode is filesystem")
        val factory = new FileSystemRecoveryModeFactory(conf, new KryoSerializer(conf))
        (factory.createPersistenceEngine(), factory.createLeaderElectionAgent(this))
      case "NONE" | _ =>
        log.info("Not recoverable")
        (new BlackHolePersistedEngine, new SingleMasterLeaderAgent(this))
    }
    persistenceEngine = persistenceEngine_
    leaderElectionAgent = leaderElectionAgent_
    super.preStart()
  }

  override def receive: Receive = standby

  private def standby: Receive = {
    case ElectedLeader =>
      httpServerActor ! ElectedLeader
      jobControllerActor ! ElectedLeader
      jobScanActor ! ElectedLeader
      context.become(active)
    case msg =>
      log.warn(s"can't handle message $msg because master not active this node")
  }

  private def active: Receive = {
    case RevokedLeadership =>
      // TODO: should kill current service or just send revoke message?
      httpServerActor ! RevokedLeadership
      jobControllerActor ! RevokedLeadership
      jobScanActor ! RevokedLeadership
      context.become(standby)
  }

  override def electedLeader(): Unit = {
    self ! ElectedLeader
  }

  override def revokedLeadership(): Unit = {
    self ! RevokedLeadership
  }
}
