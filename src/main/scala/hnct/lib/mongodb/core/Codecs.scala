package hnct.lib.mongodb.core

import com.mongodb.client.model.geojson.codecs.{MultiPointCodec, PointCodec}
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
	
	def writeSet[T](writer : BsonWriter, s : Set[T], ctx : EncoderContext)(implicit tCodec : Codec[T]) = {
		writer.writeStartArray()
		s.foreach(t => {
			tCodec.encode(writer, t, ctx)
		})
		writer.writeEndArray()
	}
	
	def writeSet[T](writer : BsonWriter, name : String, s : Set[T], ctx : EncoderContext)(implicit tCodec : Codec[T]) = {
		writer.writeName(name)
		writeSet(writer, s, ctx)
	}
	
}
