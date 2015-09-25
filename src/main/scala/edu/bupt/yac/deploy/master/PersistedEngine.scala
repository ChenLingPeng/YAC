package edu.bupt.yac.deploy.master

import edu.bupt.yac.commons.{CrawlerTaskAttempt, CrawlerTask, CrawlerJob}

import scala.reflect.ClassTag

/**
 * Created by chenlingpeng on 15/9/21.
 */


abstract class PersistedEngine {
  def persist(name: String, obj: Object)

  def unpersist(name: String)

  def readSeq[T: ClassTag](prefix: String): Seq[T]

  def read[T: ClassTag](path: String): Option[T]

  def addCrawlerJob(crawlerJob: CrawlerJob) = persist("job_" + crawlerJob.jobId, crawlerJob)

  def deleteCrawlerJob(crawlerJob: CrawlerJob) = unpersist("job_" + crawlerJob.jobId)

  def addJobTask(crawlerTask: CrawlerTask) = persist(s"job_${crawlerTask.crawlerJob.jobId}_${crawlerTask.taskId}", crawlerTask)

  def deleteJobTask(crawlerTask: CrawlerTask) = unpersist(s"job_${crawlerTask.crawlerJob.jobId}_${crawlerTask.taskId}")

  def addTaskAttempt(taskAttempt: CrawlerTaskAttempt) = persist(s"job_${taskAttempt.crawlerTask.crawlerJob.jobId}_${taskAttempt.crawlerTask.taskId}_${taskAttempt.attemptId}", taskAttempt)

  def deleteTaskAttempt(taskAttempt: CrawlerTaskAttempt) = unpersist(s"job_${taskAttempt.crawlerTask.crawlerJob.jobId}_${taskAttempt.crawlerTask.taskId}_${taskAttempt.attemptId}")

  def close() = {}
}

class BlockHolePersistedEngine extends PersistedEngine {
  override def persist(name: String, obj: Object) = {}
  override def unpersist(name: String) = {}
  override def close() = {}

  override def readSeq[T: ClassTag](prefix: String): Seq[T] = Nil

  override def read[T: ClassTag](path: String): Option[T] = None
}