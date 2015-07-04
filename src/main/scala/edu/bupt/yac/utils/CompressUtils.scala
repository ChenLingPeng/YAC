package edu.bupt.yac.utils

import java.io.File

import net.lingala.zip4j.core
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants

/**
 * Created by chenlingpeng on 15/7/3.
 */

/**
 * 压缩解压zip工具类
 */
object CompressUtils {
  def zipFolder(folder2Zip: File, zipPath: String) = {
    val zipFile = new core.ZipFile(zipPath)
    val zipParam = new ZipParameters
    zipParam.setCompressionMethod(Zip4jConstants.COMP_DEFLATE)
    zipParam.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL)
    zipFile.addFolder(folder2Zip, zipParam)
  }

  def unzipFolder(zipPath: String, zipDestFolder: String) = {
    val zipFile = new core.ZipFile(zipPath)
    zipFile.extractAll(zipDestFolder)
  }

}
