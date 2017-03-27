package hnct.lib.mongodb.impl

import java.time.LocalDateTime

import org.bson.{BsonReader, BsonWriter}
import org.bson.codecs._
import org.bson.codecs.configuration.CodecRegistries

/**
	* Created by Ryan on 11/20/2016.
	*/
object Registries {
	val primitives = CodecRegistries.fromCodecs(
		new org.bson.codecs.StringCodec,
		new org.bson.codecs.BsonBooleanCodec,
		new org.bson.codecs.DoubleCodec,
		new org.bson.codecs.FloatCodec,
		new org.bson.codecs.ObjectIdCodec,
		new org.bson.codecs.BooleanCodec,
		new org.bson.codecs.BsonObjectIdCodec,
		new org.bson.codecs.IntegerCodec,

		// self-written codecs
		IntCodec,
		LongCodec,
		DoubleCodec,
		LocalDateTimeCodec
	)
}

object IntCodec extends Codec[Int] {
	override def encode(writer: BsonWriter, value: Int, encoderContext: EncoderContext): Unit = {
		writer.writeInt32(value)
	}

	override def getEncoderClass: Class[Int] = classOf[Int]

	override def decode(reader: BsonReader, decoderContext: DecoderContext) = {
		reader.readInt32()
	}
}

object DoubleCodec extends Codec[Double] {
	override def encode(writer: BsonWriter, value: Double, encoderContext: EncoderContext): Unit = {
		writer.writeDouble(value)
	}
	
	override def getEncoderClass: Class[Double] = classOf[Double]
	
	override def decode(reader: BsonReader, decoderContext: DecoderContext) = {
		reader.readDouble()
	}
}

object LongCodec extends Codec[Long] {
	override def encode(writer: BsonWriter, value: Long, encoderContext: EncoderContext): Unit = {
		writer.writeInt64(value)
	}
	
	override def getEncoderClass: Class[Long] = classOf[Long]
	
	override def decode(reader: BsonReader, decoderContext: DecoderContext) = {
		reader.readInt64()
	}
}

object BooleanCodec extends Codec[Boolean] {
	override def encode(writer: BsonWriter, value: Boolean, encoderContext: EncoderContext): Unit = {
		writer.writeBoolean(value)
	}
	
	override def getEncoderClass: Class[Boolean] = classOf[Boolean]
	
	override def decode(reader: BsonReader, decoderContext: DecoderContext) = {
		reader.readBoolean()
	}
}

object LocalDateTimeCodec extends Codec[LocalDateTime] {
	override def encode(writer: BsonWriter, value: LocalDateTime, encoderContext: EncoderContext): Unit = {
		writer.writeString(value.toString)
	}

	override def getEncoderClass: Class[LocalDateTime] = classOf[LocalDateTime]

	override def decode(reader: BsonReader, decoderContext: DecoderContext) = {
		LocalDateTime.parse(reader.readString())
	}
}
