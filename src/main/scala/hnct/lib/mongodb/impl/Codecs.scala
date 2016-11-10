package hnct.lib.mongodb.impl

import org.bson.codecs._
import org.bson.{BsonReader, BsonType, BsonWriter}

/**
  * Created by ryan on 11/9/2016.
  *
  * Provide methods to read primitive and certain scala data structure like set, array, list
  *
  */
object Codecs {
	
	implicit val integerC = new IntegerCodec
	implicit val stringC = new StringCodec
	implicit val longC = new LongCodec
	implicit val boolC = new BooleanCodec
	implicit val doubleC = new DoubleCodec
	implicit val floatC = new FloatCodec
	
	def readSet[T](reader: BsonReader, ctx : DecoderContext)(implicit tCodec : Codec[T]) : Set[T] = {
		reader.readStartArray()
		var s = Set[T]()
		
		while (reader.readBsonType != BsonType.END_OF_DOCUMENT)
			s += tCodec.decode(reader, ctx)

		reader.readEndArray()
		s
	}

  def readSet[T](name: String, reader: BsonReader, ctx : DecoderContext)(implicit tCodec : Codec[T]) : Set[T] = {
    reader.readName(name)
    readSet[T](reader, ctx)
  }
	
	def writeSet[T](s : Set[T], writer : BsonWriter, ctx : EncoderContext)(implicit tCodec : Codec[T]): Unit = {
		writer.writeStartArray()
		s.foreach(t => {
			tCodec.encode(writer, t, ctx)
		})
		writer.writeEndArray()
	}
	
	def writeSet[T](name : String, s : Set[T], writer : BsonWriter, ctx : EncoderContext)(implicit tCodec : Codec[T]): Unit = {
		writer.writeName(name)
		writeSet[T](s, writer, ctx)
	}
	
}
