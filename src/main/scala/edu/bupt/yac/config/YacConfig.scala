package edu.bupt.yac.config

import com.typesafe.config.{Config, ConfigFactory}

/**
 * User: chenlingpeng 
 * Date: 2015/6/30 13:33.
 */
object YacConfig {
  private implicit class RichConfig(val underlying: Config) extends AnyVal {
    def getOptionalBoolean(path: String): Option[Boolean] = if (underlying.hasPath(path)) {
      Some(underlying.getBoolean(path))
    } else {
      None
    }

    def getOptionalString(path: String): Option[String] = if (underlying.hasPath(path)) {
      Some(underlying.getString(path))
    } else {
      None
    }

    def getOptionalInt(path: String): Option[Int] = if (underlying.hasPath(path)) {
      Some(underlying.getInt(path))
    } else {
      None
    }
  }

  private val config = ConfigFactory.load()
  val serverip = config.getOptionalString("yac.server.ip").getOrElse("localhost")
  val serverport = config.getOptionalInt("yac.server.port").getOrElse(1113)
  val clientport = config.getOptionalInt("yac.client.port").getOrElse(0)
  val clientthreads = config.getOptionalInt("yac.client.threads").getOrElse(Int.MaxValue)

}
