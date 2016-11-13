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
	
	/**
		* Internal trait that wraps around iterable and set
		* @tparam T
		* @tparam X
		*/
	private trait Addable[T, X[_]] { self =>
		var col : X[T]
		def +(elm : T) : Addable[T, X] = { colAdd(elm); self }
		def colAdd(elm : T) : Unit
	}
	
	private class SetAddable[T](override var col: Set[T]) extends Addable[T, Set] {
		override def colAdd(elm: T): Unit = col += elm
	}
	
	private class SeqAddable[T](override var col : Seq[T]) extends Addable[T, Seq] {
		override def colAdd(elm: T): Unit = col :+= elm
	}
	
	implicit private def convertSet[T](s : Set[T]) : SetAddable[T] = new SetAddable[T](s)
	implicit private def convertSeq[T](s : Seq[T]) : SeqAddable[T] = new SeqAddable[T](s)
	
	private def readAddable[X[_],T]
		(reader : BsonReader, ctx : DecoderContext, initial : Addable[T, X])
		(implicit tCodec : Codec[T]) : Addable[T, X] =
	{
		reader.readStartArray()
		var s = initial
		
		while (reader.readBsonType != BsonType.END_OF_DOCUMENT)
			s += tCodec.decode(reader, ctx)
		
		reader.readEndArray()
		s
	}
	
	private def writeIterator[T]
		(writer : BsonWriter, s : Iterable[T], ctx : EncoderContext)
		(implicit tCodec : Codec[T]) : Unit =
	{
		writer.writeStartArray()
		s.foreach(tCodec.encode(writer, _, ctx))
		writer.writeEndArray()
	}
	
	def readSet[T]
		(reader: BsonReader, ctx : DecoderContext)
		(implicit tCodec : Codec[T]) : Set[T] =
			readAddable[Set, T](reader, ctx, Set[T]()).col

	def readSet[T]
		(name: String, reader: BsonReader, ctx : DecoderContext)
		(implicit tCodec : Codec[T]) : Set[T] =
	{
		reader.readName(name)
		readAddable[Set, T](reader, ctx, Set[T]()).col
	}

	def writeSet[T]
		(s : Set[T], writer : BsonWriter, ctx : EncoderContext)
		(implicit tCodec : Codec[T]) : Unit =
			writeIterator(writer, s, ctx)

	def writeSet[T]
		(name : String, s : Set[T], writer : BsonWriter, ctx : EncoderContext)
		(implicit tCodec : Codec[T]) : Unit =
	{
		writer.writeName(name)
		writeSet(s, writer, ctx)
	}
	
	def readSeq[T]
		(reader: BsonReader, ctx : DecoderContext)
		(implicit tCodec : Codec[T]) : Seq[T] =
			readAddable[Seq, T](reader, ctx, Seq[T]()).col

	def readSeq[T]
		(name: String, reader: BsonReader, ctx : DecoderContext)
		(implicit tCodec : Codec[T]) : Seq[T] =
	{
		reader.readName(name)
		readAddable[Seq, T](reader, ctx, Seq[T]()).col
	}

	def writeSeq[T]
		(s : Seq[T], writer : BsonWriter, ctx : EncoderContext)
		(implicit tCodec : Codec[T]) : Unit =
			writeIterator(writer, s, ctx)

	def writeSeq[T]
		(name : String, s : Seq[T], writer : BsonWriter, ctx : EncoderContext)
		(implicit tCodec : Codec[T]) : Unit =
	{
		writer.writeName(name)
		writeSeq(s, writer, ctx)
	}

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