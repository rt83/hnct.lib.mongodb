package hnct.lib.mongodb.impl

import org.bson.codecs.{Codec, _}
import org.bson.{BsonReader, BsonType, BsonWriter}

/**
  * Created by ryan on 11/9/2016.
  *
  * Provide methods to read primitive and certain scala data structure like set, array, list
  *
  */

object Codecs {

  private val NONE = "none"

	implicit val integerC = new IntegerCodec
	implicit val stringC = new StringCodec
	implicit val longC = new LongCodec
	implicit val boolC = new BooleanCodec
	implicit val doubleC = new DoubleCodec
	implicit val floatC = new FloatCodec
  implicit val objectIdC = new ObjectIdCodec

  implicit object IntCodec extends Codec[Int] {
    override def encode(writer: BsonWriter, value: Int, encoderContext: EncoderContext): Unit = {
      writer.writeInt32(value)
    }

    override def getEncoderClass: Class[Int] = classOf[Int]

    override def decode(reader: BsonReader, decoderContext: DecoderContext) = {
      reader.readInt32()
    }
  }

  //========================================================================================
  // Internals
  //========================================================================================

  private def collectItems[T](
    reader: BsonReader,
    ctx : DecoderContext,
    collected: Seq[T]
  )(
    implicit tCodec : Codec[T]
  ): Seq[T] = reader.readBsonType match {
    case BsonType.END_OF_DOCUMENT =>
      collected.reverse
    case _ =>
      collectItems(reader, ctx, collected :+ tCodec.decode(reader, ctx))
  }

  //========================================================================================
  // General read/write of Seq[T]
  //========================================================================================

	def readSeq[T](reader: BsonReader, ctx : DecoderContext)(implicit tCodec : Codec[T]) : Seq[T] = {
		reader.readStartArray()
		val seq = collectItems(reader, ctx, Seq[T]())
		reader.readEndArray()
		seq
	}

  def readSeq[T](name: String, reader: BsonReader, ctx : DecoderContext)(implicit tCodec : Codec[T]) : Seq[T] = {
    reader.readName(name)
    readSeq[T](reader, ctx)
  }

	def writeSeq[T](s : Seq[T], writer : BsonWriter, ctx : EncoderContext)(implicit tCodec : Codec[T]): Unit = {
		writer.writeStartArray()
		s.foreach(tCodec.encode(writer, _, ctx))
		writer.writeEndArray()
	}

  def writeSeq[T](name : String, s : Seq[T], writer : BsonWriter, ctx : EncoderContext)(implicit tCodec : Codec[T]): Unit = {
    writer.writeName(name)
    writeSeq[T](s, writer, ctx)
  }

  //========================================================================================
  // General read/write of Set[T]
  //========================================================================================

  def readSet[T](reader: BsonReader, ctx : DecoderContext)(implicit tCodec : Codec[T]) : Set[T] =
    readSeq[T](reader, ctx).toSet

  def readSet[T](name: String, reader: BsonReader, ctx : DecoderContext)(implicit tCodec : Codec[T]) : Set[T] =
    readSeq[T](name, reader, ctx).toSet

  def writeSet[T](s : Set[T], writer : BsonWriter, ctx : EncoderContext)(implicit tCodec : Codec[T]): Unit =
    writeSeq[T](s.toSeq, writer, ctx)

  def writeSet[T](name: String, s : Set[T], writer : BsonWriter, ctx : EncoderContext)(implicit tCodec : Codec[T]): Unit =
    writeSeq[T](name, s.toSeq, writer, ctx)

  //========================================================================================
  // General read/write of Option[T]
  //========================================================================================

  def writeOption[T]
    (name: String, value: Option[T], writer: BsonWriter, encoderContext: EncoderContext)
    (implicit tCodec : Codec[T]): Unit =
  {
    if (value.isDefined) {
      writer.writeName(name)
      tCodec.encode(writer, value.get, encoderContext)
    }
  }

  def readOption[T]
    (name: String, reader: BsonReader, decoderContext: DecoderContext)
    (implicit tCodec : Codec[T]): Option[T] =
  {
    reader.mark()
    if (reader.readName()==name) {
      reader.reset()
      Some(tCodec.decode(reader, decoderContext))
    } else {
      reader.reset()
      None
    }
  }

  //========================================================================================
  // General operation
  //========================================================================================

  def write[T](name: String, value: T, writer: BsonWriter, encoderContext: EncoderContext)(implicit tCodec : Codec[T]): Unit = {
    writer.writeName(name)
    tCodec.encode(writer, value, encoderContext)
  }

  def read[T](reader: BsonReader, decoderContext: DecoderContext)(implicit tCodec : Codec[T]): T = {
    reader.readName()
    tCodec.decode(reader, decoderContext)
  }

  def read[T](name: String, reader: BsonReader, decoderContext: DecoderContext)(implicit tCodec : Codec[T]): T = {
    reader.readName(name)
    tCodec.decode(reader, decoderContext)
  }
	
}