package edu.bupt.yac.serializer

import java.nio.ByteBuffer

import scala.reflect.ClassTag

/**
 * Created by chenlingpeng on 15/9/22.
 */
abstract class Serializer {
  def newInstance(): SerializerInstance
}

abstract class SerializerInstance {
  def serialize[T: ClassTag](t: T): ByteBuffer

  def deserialize[T: ClassTag](bytes: ByteBuffer): T

  def deserialize[T: ClassTag](bytes: ByteBuffer, loader: ClassLoader): T
}

