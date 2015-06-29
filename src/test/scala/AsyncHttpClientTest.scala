import com.ning.http.client.{Response, AsyncCompletionHandler, AsyncHttpClient}

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._
/**
 * User: chenlingpeng 
 * Date: 2015/6/29 21:13.
 */
object AsyncHttpClientTest extends App {
  val res = Await.result(simpleAsyncGet("http://www.baidu.com"), 10 seconds)

  println(res)

  def simpleAsyncGet(url: String) = {
    val asyncHttpClient = new AsyncHttpClient()
    val promise = Promise[String]()
    asyncHttpClient.prepareGet("http://www.baidu.com").execute(new AsyncCompletionHandler[Unit] {
      override def onCompleted(response: Response) = {
        promise.success(response.getResponseBody)
      }

      override def onThrowable(t: Throwable): Unit = {
        promise.failure(t)
      }
    })
    promise.future
  }
}
