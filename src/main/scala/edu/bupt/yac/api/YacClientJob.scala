package edu.bupt.yac.api

import edu.bupt.yac.commons.YacURL
import org.apache.http.client.CookieStore

/**
 * User: chenlingpeng 
 * Date: 2015/7/2 14:41.
 */

/**
 * the trait user should implement
 */
trait YacClientJob {
  def process(url: YacURL, content: String): Unit

  def getCookieStore: CookieStore

  // before job start
  @throws(classOf[Exception])
  def preStart(): Unit

  // end the job end
  @throws(classOf[Exception])
  def postStop(): Unit
}
