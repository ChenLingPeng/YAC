import java.util.concurrent.{Executors, TimeUnit}

import edu.bupt.utils.HttpClientUtils

import scala.concurrent.{ExecutionContext, Future}

/**
 * User: chenlingpeng 
 * Date: 2015/6/30 15:20.
 */
object HttpClientUtilsTest extends App {

//  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val Exe = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  val client = HttpClientUtils.getHttpClient(1, None)
  Future {
    println(1+HttpClientUtils.executeAndExtractHttpGet(client, "http://www.qq.com").substring(0, 10))
  }
  Future {
    println(2+HttpClientUtils.executeAndExtractHttpGet(client, "http://lure.100101.cn").substring(0,10))
  }
  Future {
    println(3+HttpClientUtils.executeAndExtractHttpGet(client, "http://www.baidu.com").substring(0, 10))
  }
  Future {
    println(4+HttpClientUtils.executeAndExtractHttpGet(client, "http://www.taobao.com").substring(0, 10))
  }
  Future {
    println(5+HttpClientUtils.executeAndExtractHttpGet(client, "http://bbs.byr.cn").substring(0, 10))
  }
  Future {
    println(6+HttpClientUtils.executeAndExtractHttpGet(client, "http://weibo.com").substring(0, 10))
  }

//  TimeUnit.SECONDS.sleep(10)
  Exe.shutdown()
}
