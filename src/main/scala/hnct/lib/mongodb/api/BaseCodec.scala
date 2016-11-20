package hnct.lib.mongodb.api

import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecRegistry

import scala.reflect.ClassTag

/**
	* Created by Ryan on 11/20/2016.
	*/
abstract class BaseCodec[T](val registry : CodecRegistry) extends Codec[T] {
	
	implicit def getCodec[T](implicit t : ClassTag[T]) : Codec[T] = registry.get(t.runtimeClass.asInstanceOf[Class[T]])
	
}
