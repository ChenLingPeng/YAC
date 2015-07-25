package edu.bupt.yac.server.actor

import java.io.File

import akka.actor.{Props, Actor}

/**
 * Created by chenlingpeng on 15/7/3.
 */

/**
 * 与用户提交的job一一对应，负责处理与worker节点的交互
 */
private class JobActor(jobDir: File) extends Actor{
  override def receive: Receive = ???

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    // todo: send to worker to stop
  }
}


object JobActor {
  def props(jobDir: File) = Props(classOf[JobActor], jobDir)
}
