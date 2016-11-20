package hnct.lib.mongodb.impl

import hnct.lib.mongodb.api.BaseCodec
import org.bson.{BsonReader, BsonWriter}
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}
import org.mongodb.scala.model.geojson.{Point, Position}

import scala.collection.JavaConverters._
import Codecs._

/**
	* Created by Ryan on 11/20/2016.
	*/
object GeoJsonCodecsProvider extends CodecProvider {
	
	override def get[T](clazz: Class[T], registry: CodecRegistry): Codec[T] = {
		val codec =
			if (clazz == classOf[Point]) new PointCodec(registry)
			else null
		
		codec.asInstanceOf[Codec[T]]
	}
	
}

class PointCodec(override val registry : CodecRegistry) extends BaseCodec[Point](registry) {
	
	override def encode(writer: BsonWriter, value: Point, encoderContext: EncoderContext): Unit = {
		writer.writeStartDocument()
		writer.writeString("type", value.getType.getTypeName)
		writeSeq("coordinates", value.getCoordinates().getValues().asScala.toSeq, writer, encoderContext)
		
		/*  TODO: encode the coordinate system. Currently assume using the default coordinate system
		if (value.getCoordinateReferenceSystem != null) {
			writer.writeName("crs")
			val crs = value.getCoordinateReferenceSystem
			crs.getType.getTypeName
		}*/
		
		writer.writeEndDocument()
	}
	
	override def getEncoderClass: Class[Point] = classOf[Point]
	
	override def decode(reader: BsonReader, decoderContext: DecoderContext): Point = {
		reader.readStartDocument()
		
		val t = reader.readString("type")
		val coordinates = readSeq[Double](reader, decoderContext)
		
		// TODO: decode the coordinate system object
		
		reader.readEndDocument()
		
		Point(Position(coordinates.toArray:_*))
	}
}
