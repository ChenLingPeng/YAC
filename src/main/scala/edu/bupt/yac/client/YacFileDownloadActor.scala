package edu.bupt.yac.client

import java.io.{FileOutputStream, File}

import edu.bupt.yac.utils.CompressUtils

import scala.util.{Success, Failure}
import akka.actor.{Props, Actor}
import spray.client.pipelining._

/**
 * Created by chenlingpeng on 15/7/5.
 */
class YacFileDownloadActor extends Actor{

  import context.dispatcher
  private val pipeline = sendReceive

  val unfold: String = _

  override def receive: Receive = {
    case fileName: String =>
      val response = pipeline(Get(s"http://localhost:1113/file/$fileName"))
      response.onComplete{
        case Success(s) =>
          val content = s.entity.data.toByteArray
          val out = new FileOutputStream(new File(s"$unfold/$fileName"))
          out.write(content)
          out.flush()
          out.close()
          CompressUtils.unzipFolder(s"$unfold/$fileName",unfold)
        case Failure(f) =>
          println(f)
      }
  }
}

object YacFileDownloadActor {
  def props = Props[YacFileDownloadActor]
  lazy val fileDownloadActor = YacClientSystem().actorOf(props,"downloadactor")
  def apply() = fileDownloadActor
}
