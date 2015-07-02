import java.io.File

/**
 * User: chenlingpeng 
 * Date: 2015/7/2 08:14.
 */
object RegularTest extends App{


  // only modify when fold is recreate, all files under that dir is create/delete
  def lastModifyTest() = {
    println(new File("D:\\workspace").lastModified())
  }

  def regexTest() = {
    Array.empty[String].foreach(println)
    val timeRegex = "(\\d{1,})([s|m|h|d])".r
    val t = "34m"
    import scala.concurrent.duration._
    val res = t match {
      case timeRegex(value, "s") =>
        Some(value.toInt seconds)
      case timeRegex(value, "m") =>
        Some(value.toInt minutes)
      case timeRegex(value, "h") =>
        Some(value.toInt hours)
      case timeRegex(value, "d") =>
        Some(value.toInt days)
      case _ =>
        None
    }
    println(res)
  }

//  lastModifyTest()
  regexTest()
}
