package edu.bupt.yac.utils

import java.io.{FileInputStream, BufferedOutputStream, File, FileOutputStream}
import java.util.zip.ZipFile

import org.apache.commons.compress.archivers.zip.{ZipArchiveEntry, ZipArchiveOutputStream}
import org.apache.commons.compress.utils.IOUtils

/**
 * Created by chenlingpeng on 15/7/3.
 */

/**
 * 压缩解压zip工具类, 将来可能使用zip4j取代
 */
object CompressUtils {
  def createZip(directory: String, zipPath: String) = {
    try {
      val fOut = new FileOutputStream(new File(zipPath))
      val bOut = new BufferedOutputStream(fOut)
      val tOut = new ZipArchiveOutputStream(bOut)
      addFile2Zip(directory, tOut, "")
      tOut.finish()
      tOut.close()
      bOut.close()
      fOut.close()
    } catch {
      case t: Throwable =>
        false
    }

  }

  private def addFile2Zip(path: String, zip: ZipArchiveOutputStream, basePath: String): Unit = {
    val file = new File(path)
    val entryName = basePath+file.getName
    val zipEntry = new ZipArchiveEntry(file, entryName)
    zip.putArchiveEntry(zipEntry)
    if(file.isFile){
      val fInputStream = new FileInputStream(file)
      IOUtils.copy(fInputStream, zip)
      zip.closeArchiveEntry()
      IOUtils.closeQuietly(fInputStream)
    }else{
      zip.closeArchiveEntry()
      file.listFiles().foreach{f => addFile2Zip(f.getAbsolutePath, zip, entryName+"/")}
    }
  }

  def extractZip(zip: File, destDir: String) = {
//    try {
      val unzip = new File(destDir)
      unzipFolder(zip, unzip)
//    } catch {
//      case t: Throwable => println(t)
//    }

  }

  private def unzipFolder(zip: File, zipDestFolder: File) = {
    val zipFile = new ZipFile(zip)
    val entries = zipFile.entries()
    while(entries.hasMoreElements){
      val entry = entries.nextElement()
      val entryDestination = new File(zipDestFolder.getAbsolutePath,  entry.getName)
      if (entry.isDirectory)
        entryDestination.mkdirs()
      else {
        entryDestination.getParentFile.mkdirs()
        val in = zipFile.getInputStream(entry)
        val out = new FileOutputStream(entryDestination)
        IOUtils.copy(in, out)
        IOUtils.closeQuietly(in)
        out.close()
      }
    }
    zipFile.close()

  }

}
