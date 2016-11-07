package hnct.lib.mongodb.api

import org.bson.codecs.configuration.CodecRegistry
import org.bson.conversions.Bson
import org.mongodb.scala._
import scala.reflect.runtime.universe._

/**
	* Created by Ryan on 11/6/2016.
	*/
trait MongoDb {
	
	val conn : MongoClient
	
	// the codecs used for the applications
	val codecs : CodecRegistry
	
	val dbName : String
	
	val db : MongoDatabase
	
	def col[T](name : String) : MongoCollection[T]
	
	def fetch[T](nReturn : Int = 0)(implicit t : TypeTag[T]) : Seq[T]
	
	def fetch[T](query: Bson, nReturn: Int = 0)(implicit t : TypeTag[T]) : Seq[T]
	
}
