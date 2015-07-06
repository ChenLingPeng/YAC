package edu.bupt.yac.server

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import edu.bupt.yac.config.YacConfig
import edu.bupt.yac.server.actor.{JobScanActor, JobControllerActor}

import scala.concurrent.ExecutionContext

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 15:20.
 */
object YacSystem {
  lazy implicit val system = ActorSystem(YacConfig.actorSystemName,YacConfig.config.getConfig("yac.server.SeverSys"))
  lazy implicit val exec = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
  def apply() = system

  def main (args: Array[String]) {
    YacSystem()
    HttpServer.serverStart()
    // touch to start
    JobControllerActor()
    JobScanActor()
  }
}
