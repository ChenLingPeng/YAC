package edu.bupt.yac.commons

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 17:54.
 */


case object Tick

case object JobScan

case class NodeInfo(threads: Int, bandwidth: Double)

case class HeartBeat(ip: String, port: Int, info: Some[NodeInfo])

// 持久化命令
case object Persist