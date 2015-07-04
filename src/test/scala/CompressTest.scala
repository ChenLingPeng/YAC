import java.io.File

import edu.bupt.yac.utils.CompressUtils

/**
 * Created by chenlingpeng on 15/7/3.
 */
object CompressTest extends App {
  def compress() = {
    CompressUtils.zipFolder(new File("/Users/chenlingpeng/workspace/scala/untitled"), "/Users/chenlingpeng/workspace/t.zip")
  }

  def decompress() = {
    CompressUtils.unzipFolder("/Users/chenlingpeng/workspace/t.zip","/Users/chenlingpeng/workspace/")
  }
//  compress()
 decompress()

}
