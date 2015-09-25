package edu.bupt.yac.server

import YacSystem._
import akka.io.IO
import edu.bupt.yac.client.YacFileDownloadActor
import edu.bupt.yac.commons.{RevokedLeadership, ElectedLeader}
import org.apache.commons.codec.binary.Base64

import org.apache.log4j.Logger
import akka.actor._
import spray.http.HttpHeaders.RawHeader
import spray.http.StatusCodes
import spray.routing.{RoutingSettings, HttpService}
import spray.can.Http
import spray.routing.directives.CachingDirectives._
import scala.concurrent.ExecutionContextExecutor

/**
 * Created by chenlingpeng on 15/7/5.
 */

object HttpServer {
  val log = Logger.getLogger(this.getClass)
  def props() = Props[HttpServerActor]
}

class HttpServerActor extends Actor with YacHttpService{
  import HttpServer.log
  override def actorRefFactory = context
  implicit val settings = RoutingSettings.default(context)
  def receive = notService

  def notService: Actor.Receive = {
    case ElectedLeader =>
      runRoute(yacRoute)
    case msg =>
      log.info(s"$msg message not handled because not a leader here...")
  }

  def onService: Actor.Receive = {
    runRoute(yacRoute) orElse listenForShutdownService
  }

  def listenForShutdownService: Actor.Receive = {
    case RevokedLeadership =>
      context.become(notService)
  }
}

trait YacHttpService extends HttpService{
  import HttpServer.log
  val zipTmpDir: String = "server/ziptmpfile"
  implicit def executionContext: ExecutionContextExecutor = actorRefFactory.dispatcher

  def auth(rawStr: String) {
    val decode = new String(Base64.decodeBase64(rawStr.getBytes))

  }

  val yacRoute = {
    get {
      pathSingleSlash {
        complete("hello, index")
      } ~
      path("ping") {
        complete("pong")
      } ~
        (path("file" / Segment) & optionalHeaderValueByName("Authorization") ) {
          (fileName, header) =>
          {
            println(header.getOrElse("no header"))
            header.flatMap{
              _.split(" ").toList match {
                case "Basic" :: name_pass_encoded :: Nil =>
                  new String(Base64.decodeBase64(name_pass_encoded.getBytes)).split(":").toList match {
                    case YacFileDownloadActor.authName :: YacFileDownloadActor.authPasswd :: Nil =>
                      log.info(fileName + " commanded!!! with auth " + header)
                      Some(getFromFile(s"$zipTmpDir/$fileName"))
                    case _ => None
                  }
                case _ => None
              }
            }.getOrElse(
                respondWithHeader(RawHeader("WWW-Authenticate","Basic realm=\"Only use for Yac Client\""))
                  (complete(StatusCodes.Unauthorized -> "not authorized"))
              )
          }
      }  ~
      path("info") {
        // TODO: restful api
        complete("some server info")
      }
    }
  }

  lazy val simpleRouteCache = routeCache()
}
