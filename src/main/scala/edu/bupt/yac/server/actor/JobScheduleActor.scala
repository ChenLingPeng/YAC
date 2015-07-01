package edu.bupt.yac.server.actor

import java.io.File

import akka.actor.{Props, Actor}
import edu.bupt.yac.commons.JobScan
import edu.bupt.yac.server.YacSystem
import org.apache.log4j.Logger

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 18:14.
 */

/**
 * Job调度相关，包括对目录的定期扫描，添加，删除等
 */
class JobScheduleActor extends Actor{
  val log = Logger.getLogger(JobScheduleActor.getClass)
  val jobs =  scala.collection.mutable.Map[String, Long]()
  val basePath = System.getProperty("user.dir")
  log.info(s"base dir: $basePath")

  import scala.concurrent.duration._
  context.system.scheduler.schedule(5 minutes,5 minutes,self, JobScan)

  override def receive: Receive = {
    case JobScan =>
      scan()

  }

  // job should have .jar yacjob.properties seeds.txt
  private def scan() = {
    val baseDir = new File(basePath)
    if(baseDir.exists() && baseDir.isDirectory){
      baseDir.listFiles().foreach{jobDir =>

      }
    }
  }
}

object JobScheduleActor{
  private def props = Props[JobScheduleActor]
  lazy val jobScheduleActor = YacSystem().actorOf(props)
}