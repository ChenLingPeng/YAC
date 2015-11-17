package edu.bupt.yac.commons

import java.io.File

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 17:54.
 */


case object Tick

case class NodeInfo(threads: Int, bandwidth: Double)

case class HeartBeat(ip: String, port: Int, info: Option[NodeInfo])

case class Register(ip: String, port: Int, mac: String, timestamp: String)

case class JobAdd(jobDir: File)
case class JobDelete(jobDir: String)

case class JobScan(jobDir: File)
case class YacURL(url: String, jobName: String)
case class YacURLResult(url: String, seed: Boolean, content: String, code: Int, retry: Int)
// 持久化命令
case object Persist

case class MachineInfo(cpu: Int, memoryMByte: Long) // work节点信息

sealed trait LeaderElect
case object ElectedLeader extends LeaderElect
case object RevokedLeadership extends LeaderElect
