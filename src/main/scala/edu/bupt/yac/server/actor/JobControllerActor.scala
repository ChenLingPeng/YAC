package edu.bupt.yac.server.actor

import java.io.File

import akka.actor.{Terminated, Props, Actor}
import edu.bupt.yac.commons.{Register, JobDelete, JobAdd}
import edu.bupt.yac.server.YacSystem
import org.apache.log4j.Logger
import scala.collection.mutable
import scala.io.Source

/**
 * User: chenlingpeng 
 * Date: 2015/7/2 11:09.
 */

/**
 * 负责Job的创建，删除，定时启动等
 */
class JobControllerActor extends Actor{
  val log = Logger.getLogger(this.getClass)
  log.info("JobControllerActor start")

  val jobsWaitingForRestart = mutable.Map[String, File]()

  override def receive: Receive = {
    case JobAdd(jobDir) =>
      // job文件夹如果创建或者修改时都会触发
      val jobOption = context.child(jobDir.getName)
      jobOption match {
        case Some(ref) =>
          // 存在老的actor正在运行
          context stop ref
          jobsWaitingForRestart put (jobDir.getName, jobDir)
        case None =>
          // 不存在，如果在waiting list，则表示之前有类似的请求，移除即可
          context.watch(context.actorOf(JobActor.props(jobDir), jobDir.getName))
          jobsWaitingForRestart remove jobDir.getName

      }
    case JobDelete(jobDir) =>
      // job文件夹被删除
      context.child(jobDir).foreach(context stop)
    case Terminated(child) =>
      val jobName = child.path.name
      context.unwatch(child) // 需要吗
      jobsWaitingForRestart.remove(jobName).foreach{jobDir =>
        context.watch(context.actorOf(JobActor.props(jobDir), jobDir.getName))
      }
    case str: String =>
      println(s"$str from ${sender().path.address}")
      sender() ! "send back!"
    case iter: List[_] =>
      iter.foreach(println)
    case Register =>
      log.info(s"register from ${sender().path.address}")

  }
}

object JobControllerActor {
  private def props = Props[JobControllerActor]

  lazy val jobControllerActor = YacSystem().actorOf(props, "jobcontroller")

  def apply() = jobControllerActor
}
