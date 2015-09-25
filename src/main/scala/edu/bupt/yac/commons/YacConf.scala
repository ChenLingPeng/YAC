package edu.bupt.yac.commons

import com.typesafe.config.ConfigFactory

/**
 * Created by chenlingpeng on 15/9/21.
 */
class YacConf {
  val config = ConfigFactory.load()
  def get(key: String, default: String): String = {
    if(config.hasPath(key)) {
      config.getString(key)
    } else {
      default
    }
  }
}
