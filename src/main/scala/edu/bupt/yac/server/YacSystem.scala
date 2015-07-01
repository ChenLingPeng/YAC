package edu.bupt.yac.server

import akka.actor.ActorSystem

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 15:20.
 */
object YacSystem {
  lazy val system = ActorSystem("YACSystem")
  def apply() = system

  def main (args: Array[String]) {

  }
}
