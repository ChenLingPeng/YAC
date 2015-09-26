package edu.bupt.yac.server

import java.io.{FileOutputStream, File}

import YacSystem._
import edu.bupt.yac.client.YacFileDownloadActor
import edu.bupt.yac.commons.{RevokedLeadership, ElectedLeader}
import org.apache.commons.codec.binary.Base64

import org.apache.log4j.Logger
import akka.actor._
import spray.http.HttpHeaders.RawHeader
import spray.http.{MultipartFormData, MediaTypes, StatusCodes}
import spray.routing._
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
      context.become(onService)
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
  val zipUpload: String = "server/upload"
  new File(zipTmpDir).mkdirs()
  new File(zipUpload).mkdirs()
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
    } ~ post {
      path("file") {
        respondWithMediaType(MediaTypes.`application/json`) {
          entity(as[MultipartFormData]) { formData =>
            // curl -F filename=@xxx.zip http://localhost:1113
            val body = formData.get("filename").get
            val fileName = body.filename.get
            val data = body.entity.data.toByteArray
            val file = new File(zipUpload, fileName)
            val writer = new FileOutputStream(file)
            writer.write(data)
            writer.close()
            complete("""{"status":0}""")
          }
        }
      }
    }
  }

  lazy val simpleRouteCache = routeCache()
}
