package edu.bupt.yac.server.actor

import java.io.{FilenameFilter, File}
import java.net.{URL, URLClassLoader}

import akka.actor.{Props, Actor}
import edu.bupt.yac.api.YacClientJob
import edu.bupt.yac.commons.{JobAdd, JobScan}
import edu.bupt.yac.config.{Constants, JobConfigParser}
import edu.bupt.yac.server.YacSystem
import org.apache.log4j.Logger

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 18:14.
 */

/**
 * Job扫描相关，包括对目录的定期扫描，添加，删除等
 */
class JobScanActor extends Actor {
  val log = Logger.getLogger(this.getClass)
  log.info("JobScanActor start")

  val jobs = scala.collection.mutable.Map[String, Long]().withDefaultValue(0L)
  val basePath = System.getProperty("user.dir")
  log.info(s"base dir: $basePath")

  import scala.concurrent.duration._
  import YacSystem.exec

  context.system.scheduler.schedule(3 seconds, 5 minutes, self, JobScan)

  override def receive: Receive = {
    case JobScan =>
      log.info("start scan")
      scan().foreach(JobControllerActor() ! JobAdd(_))
      // TODO: if the fold is modify or delete, should send JobDelete signal
    log.info("end scana")
  }

  // 扫描job需要文件: .jar yacjob.properties seeds.txt
  private def scan() = {
    val baseDir = new File(basePath)
    if (baseDir.exists() && baseDir.isDirectory) {
      val jobsValid = baseDir.listFiles().filter(file =>
        file.isDirectory
        && file.listFiles().length == 3
        && file.listFiles().exists(file => file.getName == Constants.seedFileName)
        && file.listFiles().exists(file => file.getName == Constants.propertiesFileName)
        && file.listFiles().exists(file => file.getName.endsWith(".jar"))
      )
      jobsValid.flatMap{jobDir =>
        val lastModify = jobDir.listFiles().map(_.lastModified()).max
        val oldModify = jobs(jobDir.getName)
        if(lastModify>oldModify){
          jobs.put(jobDir.getName, lastModify)
          val valid = check(jobDir)
          if(valid)
            Some(jobDir)
          else
            None
        } else {
          None
        }
      }

    } else {
      Array.empty[File]
    }
  }

  private def check(jobDir: File) = {
    val conf = new JobConfigParser(jobDir.getPath+"/"+Constants.propertiesFileName)
    conf.check && {
      try {
        val jarFile = jobDir.listFiles(new FilenameFilter {
          override def accept(dir: File, name: String): Boolean = name.endsWith(".jar")
        })(0)
        val classLoader = new URLClassLoader(Array[URL] {
          new URL("file:" + jarFile.getAbsoluteFile)
        }, Thread.currentThread().getContextClassLoader)
        val `class` = classLoader.loadClass(conf.getClassPath.get)
        `class`.newInstance().asInstanceOf[YacClientJob]
        true
      } catch {
        case t: Throwable =>
          log.error(t.getMessage, t)
          false
      }
    }
  }
}

object JobScanActor {
  private def props = Props[JobScanActor]

  lazy val jobScanActor = YacSystem().actorOf(props)

  def apply() = jobScanActor
}
