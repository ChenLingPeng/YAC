package edu.bupt.utils

import java.io.{IOException, InterruptedIOException}
import java.net.UnknownHostException
import java.nio.charset.CodingErrorAction
import java.util
import javax.net.ssl.SSLException

import org.apache.http.client.methods.HttpGet
import org.apache.http.protocol.HttpContext
import org.apache.http._
import org.apache.http.client.config.{CookieSpecs, RequestConfig}
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.client.{HttpRequestRetryHandler, CookieStore}
import org.apache.http.config.{ConnectionConfig, MessageConstraints}
import org.apache.http.conn.ConnectTimeoutException
import org.apache.http.impl.client.{HttpClients, CloseableHttpClient}
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.message.BasicHeader
import org.apache.http.util.EntityUtils

/**
 * User: chenlingpeng 
 * Date: 2015/6/30 13:32.
 */
object HttpClientUtils {
  def getHttpClient(poolSize: Int, cookieStore: Option[CookieStore]): CloseableHttpClient = {
    val messageConstraints = MessageConstraints.custom
      .setMaxHeaderCount(200)
      .setMaxLineLength(5000)
      .build

    val connectionConfig = ConnectionConfig.custom
      .setMalformedInputAction(CodingErrorAction.IGNORE)
      .setUnmappableInputAction(CodingErrorAction.IGNORE)
      .setCharset(Consts.UTF_8)
      .setBufferSize(64 * 1024)
      .setMessageConstraints(messageConstraints)
      .build

    val globalConfig = RequestConfig.custom
      .setCookieSpec(CookieSpecs.BEST_MATCH)
      .setCircularRedirectsAllowed(false)
      .setRedirectsEnabled(true)
      .setConnectTimeout(10000)
      .setSocketTimeout(10000)
      .build

    val cm = new PoolingHttpClientConnectionManager
    cm.setMaxTotal(poolSize)
    cm.setDefaultMaxPerRoute(poolSize)
    cm.setDefaultConnectionConfig(connectionConfig)
    val headers = new util.ArrayList[Header](5)
    import HttpClientParams._
    headers.add(new BasicHeader("User-Agent", defaultUserAgent))
    headers.add(new BasicHeader("Accept", defaultAccept))
    headers.add(new BasicHeader("Accept-Encoding", defaultAcceptEncoding))
    headers.add(new BasicHeader("Accept-Language", defaultAcceptLanguage))
    headers.add(new BasicHeader("Keep-Alive", "115"))

    HttpClients.custom.setConnectionManager(cm)
      .setDefaultRequestConfig(globalConfig)
      .setDefaultCookieStore(cookieStore.orNull)
      .setRetryHandler(new HttpRequestRetryHandler() {
      def retryRequest(exception: IOException, executionCount: Int, context: HttpContext): Boolean = {
        if (executionCount >= 2) {
          return false
        }
        if (exception.isInstanceOf[InterruptedIOException]) {
          return false
        }
        if (exception.isInstanceOf[UnknownHostException]) {
          return false
        }
        if (exception.isInstanceOf[ConnectTimeoutException]) {
          return false
        }
        if (exception.isInstanceOf[SSLException]) {
          return false
        }
        !HttpClientContext.adapt(context).getRequest.isInstanceOf[HttpEntityEnclosingRequest]
      }
    }).setDefaultHeaders(headers).build
  }

  @throws[IOException]
  def executeHttpGet(httpClient: CloseableHttpClient, url: String, proxy: Option[String] = None) = {

    val httpGet = new HttpGet(url)
    proxy.foreach { proxy =>
      val ipPort = proxy.split(":")
      val httpHost = new HttpHost(ipPort(0), ipPort(1).toInt)
      val config = RequestConfig.custom()
        .setProxy(httpHost)
        .setConnectTimeout(10000)
        .setSocketTimeout(1000)
        .setRedirectsEnabled(true)
        .setCircularRedirectsAllowed(false)
        .setCookieSpec(CookieSpecs.BEST_MATCH)
        .build()
      httpGet.setConfig(config)
    }
    httpClient.execute(httpGet)
  }

  def executeAndExtractHttpGet(httpClient: CloseableHttpClient, url: String, proxy: Option[String] = None) = {
    val response = executeHttpGet(httpClient, url, proxy)
    val entity = response.getEntity
    val body = EntityUtils.toString(entity)
    EntityUtils.consume(entity)
    response.close()
    body
  }
}

case object HttpClientParams {
  val defaultUserAgent: String = "Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1"
  val defaultAccept: String = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
  val defaultAcceptEncoding: String = "gzip,deflate,sdch,identity"
  val defaultAcceptLanguage: String = "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"
}