package edu.bupt.yac.serializer

import java.nio.ByteBuffer

import com.esotericsoftware.kryo.io.{Input, Output}
import com.twitter.chill.{Kryo, ScalaKryoInstantiator}
import edu.bupt.yac.commons.YacConf

import scala.reflect.ClassTag

/**
 * Created by chenlingpeng on 15/9/22.
 */
class KryoSerializer(conf: YacConf) extends Serializer with Serializable{
  private val BUFFER_SIZE = 64 * 1024
  private val MAX_BUFFER_SIZE = 64 * 1024 * 1024

  override def newInstance(): SerializerInstance = new KryoSerializerInstance(this)

  private[serializer] def newKryo(): Kryo = {
    val instantiator = new ScalaKryoInstantiator
    instantiator.setRegistrationRequired(false)
    instantiator.newKryo()
  }

  private[serializer] def newKryoOutput() = {
    new Output(BUFFER_SIZE, MAX_BUFFER_SIZE)
  }
}

class KryoSerializerInstance(ks: KryoSerializer) extends SerializerInstance {
  private lazy val output = ks.newKryoOutput()
  private lazy val input = new Input()

  override def serialize[T: ClassTag](t: T): ByteBuffer = {
    output.clear()
    ks.newKryo().writeClassAndObject(output, t)
    ByteBuffer.wrap(output.toBytes)
  }

  override def deserialize[T: ClassTag](bytes: ByteBuffer): T = {
    val kryo = ks.newKryo()
    input.setBuffer(bytes.array)
    kryo.readClassAndObject(input).asInstanceOf[T]
  }

  override def deserialize[T: ClassTag](bytes: ByteBuffer, loader: ClassLoader): T = {
    val kryo = ks.newKryo()
    kryo.setClassLoader(loader)
    input.setBuffer(bytes.array)
    kryo.readClassAndObject(input).asInstanceOf[T]
  }
}
