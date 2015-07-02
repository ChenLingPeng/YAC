package edu.bupt.yac.server

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import edu.bupt.yac.server.actor.{JobScanActor, JobControllerActor}

import scala.concurrent.ExecutionContext

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 15:20.
 */
object YacSystem {
  lazy val system = ActorSystem("YACSystem")
  lazy implicit val exec = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
  def apply() = system

  def main (args: Array[String]) {
    YacSystem()
    // touch to start
    JobControllerActor()
    JobScanActor()
  }
}
