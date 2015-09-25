package edu.bupt.yac.deploy.master

import java.io.{FileInputStream, FileOutputStream, File}
import java.nio.ByteBuffer

import edu.bupt.yac.serializer.Serializer
import org.apache.log4j.Logger

import scala.reflect.ClassTag

/**
 * Created by chenlingpeng on 15/9/22.
 */
class FileSystemPersistenceEngine(val dir: String, serializer: Serializer) extends PersistedEngine {
  val log = Logger.getLogger(classOf[FileSystemPersistenceEngine])

  new File(dir).mkdir()

  override def persist(name: String, obj: Object): Unit = {
    serializeToFile(new File(dir + File.separator + name), obj)
  }

  override def unpersist(name: String): Unit = {
    new File(dir + File.separator + name).delete()
  }

  override def readSeq[T: ClassTag](prefix: String): Seq[T] = {
    val files = new File(dir).listFiles().filter(_.getName.startsWith(prefix))
    files.map(deserializeFromFile[T]).flatten.toSeq
  }

  override def read[T: ClassTag](path: String): Option[T] = {
    deserializeFromFile(new File(dir+File.separator+path))
  }

  private def serializeToFile(file: File, obj: AnyRef) = {
    if(!file.createNewFile()) {
      val fileOut = new FileOutputStream(file)
      try {
        val serialized = serializer.newInstance().serialize(obj)
        fileOut.write(serialized.array)
      } finally {
        fileOut.close()
      }
    }
  }

  private def deserializeFromFile[T](file: File)(implicit m: ClassTag[T]): Option[T] = {
    val fileIn = new FileInputStream(file)
    try {
      val data = new Array[Byte](file.length().toInt)
      val size = fileIn.read(data)
      println(s"length: ${file.length()}, size: $size")
      Some(serializer.newInstance().deserialize[T](ByteBuffer.wrap(data)))
    } catch {
      case _: Throwable =>
        None
    } finally {
      fileIn.close()
    }
  }
}
