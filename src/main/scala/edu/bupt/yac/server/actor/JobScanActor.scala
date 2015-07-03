package edu.bupt.yac.server.actor

import java.io.{FilenameFilter, File}
import java.net.{URL, URLClassLoader}

import akka.actor.{Props, Actor}
import edu.bupt.yac.api.YacClientJob
import edu.bupt.yac.commons.{JobDelete, JobAdd, JobScan}
import edu.bupt.yac.config.{Constants, JobConfigParser}
import edu.bupt.yac.server.YacSystem
import org.apache.commons.io.monitor.{FileAlterationListenerAdaptor, FileAlterationMonitor, FileAlterationObserver}
import org.apache.log4j.Logger

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 18:14.
 */

/**
 * Job扫描相关，包括对目录的定期扫描，添加，删除等
 */
private class JobScanActor extends Actor {
  val log = Logger.getLogger(this.getClass)
  log.info("JobScanActor start")

  val jobs = scala.collection.mutable.Map[String, Long]().withDefaultValue(0L)
  val basePath = System.getProperty("user.dir")
  log.info(s"base dir: $basePath")

  override def receive: Receive = {
    case JobScan(jobDir) =>
      log.info("start scan")
      val valid = check(jobDir)
      if(valid) JobControllerActor() ! JobAdd(jobDir)
      // TODO: if the fold is modify or delete, should send JobDelete signal
    log.info("end scana")
    case JobDelete(jobDir) =>
      JobControllerActor() ! JobDelete(jobDir)
  }

  // 扫描job需要文件: .jar yacjob.properties seeds.txt
  private def check(jobDir: File) = {
      jobDir.listFiles().length==3 &&
        jobDir.listFiles().exists(_.getName == Constants.seedFileName) &&
        jobDir.listFiles().exists(_.getName == Constants.propertiesFileName) &&
        jobDir.listFiles().exists(_.getName.endsWith(".jar")) &&
        checkRaw(jobDir)
  }

  private def checkRaw(jobDir: File) = {
    val conf = JobConfigParser(jobDir.getPath+"/"+Constants.propertiesFileName)
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

  private def monitorDir() = {
    val pollingInterval: Long = 5*1000
    val jobsDir = new File(basePath+"/jobs")
    if(!jobsDir.exists()) jobsDir.mkdir()
    val observer = new FileAlterationObserver(jobsDir)
    val monitor = new FileAlterationMonitor(pollingInterval)
    val listener = new FileAlterationListenerAdaptor(){
      override def onDirectoryChange(directory: File): Unit = {
        log.info(directory.getName+" change")
        self ! JobScan(directory)
      }

      override def onFileChange(file: File): Unit = {
        log.info(file.getName+" change")
        self ! JobScan(file.getParentFile)
      }

      override def onDirectoryDelete(directory: File): Unit = {
        log.info(directory.getName+" delete")
        self ! JobDelete(directory.getName)
      }

      override def onDirectoryCreate(directory: File): Unit = {
        log.info(directory.getName+" create")
        self ! JobScan(directory)
      }

    }
    observer.addListener(listener)
    monitor.addObserver(observer)
    monitor.start()
  }

  monitorDir()
}

object JobScanActor {
  private def props = Props[JobScanActor]

  lazy val jobScanActor = YacSystem().actorOf(props)

  def apply() = jobScanActor
}

