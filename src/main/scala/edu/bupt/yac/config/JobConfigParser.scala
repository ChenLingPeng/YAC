package edu.bupt.yac.config

import java.io.File

import scala.io.Source

/**
 * User: chenlingpeng 
 * Date: 2015/7/1 17:38.
 */
class JobConfigParser(filepath: String) {

  val file = new File(filepath)
  val valid = file.exists() && file.isFile


  val confMap = {
      Source.fromFile(file).getLines().flatMap{line =>
        val kv = line.split("=")
        if(kv.length==2) Some((kv(0).trim,kv(1).trim))
        else None
      }.foldLeft(Map[String,String]()){case (map,(k,v))=>
        map + ((k,v))
      }
  }

  def getXXX(xxx: String) = ???
}
