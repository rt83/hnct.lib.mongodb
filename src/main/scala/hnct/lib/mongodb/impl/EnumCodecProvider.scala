package hnct.lib.mongodb.impl

import org.bson.{BsonReader, BsonWriter}
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}

object EnumCodecProvider {
  def create[E <: Enum[E]](c : Class[E]) = new CodecProvider {

    override def get[T](clazz: Class[T], registry: CodecRegistry): Codec[T] = {

      val codec = if (c == clazz) {
        new Codec[E] {
          override def encode(writer: BsonWriter, value: E, encoderContext: EncoderContext): Unit = {
            writer.writeString(value.toString())
          }

          override def getEncoderClass: Class[E] = c

          override def decode(reader: BsonReader, decoderContext: DecoderContext): E = {
            return Enum.valueOf(c, reader.readString())
          }
        }
      } else null

      codec.asInstanceOf[Codec[T]]
    }
  }
}
