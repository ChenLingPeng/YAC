package edu.bupt.yac.client

import akka.actor.ActorSystem

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 15:42.
 */
object YacClientSystem extends App {

  val clientSystem = ActorSystem("client")

  def apply() = clientSystem

//  YacFileDownloadActor() ! "tmp.zip"

}

