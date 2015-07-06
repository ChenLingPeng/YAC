package edu.bupt.yac.client

import java.net.InetAddress

import scala.concurrent.duration._

import akka.actor._
import edu.bupt.yac.commons.{HeartBeat, Tick}
import edu.bupt.yac.config.YacConfig
import org.apache.log4j.Logger

/**
 * Created by chenlingpeng on 15/7/6.
 */

class YacClientNodeManagerActor extends Actor{
  val log = Logger.getLogger(this.getClass)

  val identifyId = 1
  val remoteNodeAddr = s"akka.tcp://${YacConfig.actorSystemName}@${YacConfig.serverip}:${YacConfig.serverport}/user/jobcontroller"
  context.actorSelection(remoteNodeAddr) ! Identify(identifyId)
  context.setReceiveTimeout(5 seconds)

  override def receive: Receive = {
    case ActorIdentity(`identifyId`, Some(ref)) =>
      context.watch(ref)
      context.become(heartbeat(ref))
      context.setReceiveTimeout(Duration.Undefined)
      context.system.scheduler.schedule(5 seconds, 5 seconds, self, Tick)
    case ActorIdentity(`identifyId`, None) =>
      log.error("server may not started...")
      context.system.shutdown()
    case ReceiveTimeout =>
      log.warn(s"receive timeout when identify remote master node $remoteNodeAddr, retry...")
      context.actorSelection(remoteNodeAddr) ! Identify(identifyId)
  }

  def heartbeat(remoteActor: ActorRef): Receive = {
    case Tick =>
      remoteActor ! HeartBeat(InetAddress.getLocalHost.getHostAddress, YacConfig.clientport, None)
  }
}
