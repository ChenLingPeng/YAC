package edu.bupt.yac.config

import java.io.{FilenameFilter, File}

import org.apache.commons.io.Charsets
import org.apache.http.util.CharsetUtils

import scala.io.Source

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 17:38.
 */
class JobConfigParser(filepath: String) {

  val file = new File(filepath)

  private val confMap = {
      Source.fromFile(file).getLines().flatMap{line =>
        val kv = line.split("=")
        if(kv.length==2) Some((kv(0).trim,kv(1).trim))
        else None
      }.foldLeft(Map[String,Option[String]]()){case (map,(k,v))=>
        map + ((k,Some(v)))
      }.withDefaultValue(None)
  }

  private def getField(field: String) = confMap(field)

  import JobConfigParser._
  def getJobName = getField(fieldJobName)
  def getClassPath = getField(fieldClassPath)
  def isProxy = getField(fieldProxy).getOrElse("false") == "true"
  def getThreadNum = getField(fieldThreadNum).getOrElse("1").toInt
  def getCharset = getField(charset).getOrElse("utf-8")
  def isRepeat = getField(repeat).getOrElse("false") == "true"

  private val timeRegex = "(\\d{1,})([s|m|h|d])".r
  import scala.concurrent.duration._
  def delayTime = getField(delay).flatMap {
    case timeRegex(value, "s") =>
      Some(value.toInt seconds)
    case timeRegex(value, "m") =>
      Some(value.toInt minutes)
    case timeRegex(value, "h") =>
      Some(value.toInt hours)
    case timeRegex(value, "d") =>
      Some(value.toInt days)
    case _ =>
      None
  }

  def check = {
    // 如果repeat，一定需要设置delay字段
    getJobName.isDefined && getClassPath.isDefined && (!isRepeat || delayTime.nonEmpty)
  }


}

object JobConfigParser{
  val fieldJobName = "jobname"
  val fieldClassPath = "classpath"
  // 可能已经不需要
  val fieldThreadNum = "threadnumber"
  val fieldProxy = "proxy"
  //运行周期, 1000s   1000m  24h 1d
  val delay = "delay"
  val repeat = "repeat"
  val charset = "charset"
}
