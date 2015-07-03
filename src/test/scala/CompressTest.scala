import java.io.File

import edu.bupt.yac.utils.CompressUtils

/**
 * Created by chenlingpeng on 15/7/3.
 */
object CompressTest extends App {
  def compress() = {
//    val c = GzipComp
    CompressUtils.createZip("/Users/chenlingpeng/workspace/scala/untitled","/Users/chenlingpeng/workspace/t.zip")
  }

//  compress()

  def decompress() = {
    CompressUtils.extractZip(new File("/Users/chenlingpeng/workspace/t.zip"), "/Users/chenlingpeng/workspace/")
  }
  decompress()
}
