package edu.bupt.yac.server

import YacSystem._
import akka.io.IO

import org.apache.log4j.Logger
import akka.actor._
import spray.routing.{RoutingSettings, HttpService}
import spray.can.Http
import spray.routing.directives.CachingDirectives._
import scala.concurrent.ExecutionContextExecutor

/**
 * Created by chenlingpeng on 15/7/5.
 */

object HttpServer {
  val log = Logger.getLogger(this.getClass)
  def serverStart() = {
    val service = YacSystem().actorOf(Props[HttpServerActor], "httpserver")
    IO(Http) ! Http.Bind(service, "localhost", port = 1113)
    log.info("http server started")
  }

}

class HttpServerActor extends Actor with YacHttpService{
  override def actorRefFactory = context
  implicit val settings = RoutingSettings.default(context)
  def receive = runRoute(yacRoute)
}

trait YacHttpService extends HttpService{
  import HttpServer.log
  val zipTmpDir: String = "server/ziptmpfile"
  implicit def executionContext: ExecutionContextExecutor = actorRefFactory.dispatcher
  val yacRoute = {
    get {
      pathSingleSlash {
        complete("hello, index")
      } ~
      path("ping") {
        complete("pong")
      } ~
      path("file" / Segment) {
        fileName =>
          log.info(fileName+" commanded!!!")
          getFromFile(s"$zipTmpDir/$fileName")
      } ~
      path("info") {
        // TODO: restful api
        complete("some server info")
      }
    }
  }

  lazy val simpleRouteCache = routeCache()
}
