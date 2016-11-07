package hnct.lib.mongodb.core_new

import hnct.lib.mongodb.api.MongoDb
import org.bson.codecs.configuration.CodecRegistry
import org.bson.conversions.Bson
import org.mongodb.scala.{FindObservable, MongoClient, MongoCollection, MongoDatabase}

import scala.reflect.runtime.universe._

/**
	* Created by Ryan on 11/6/2016.
	*/
class MongoDbImpl(
   host : String, port : Int,
   override val codecs: CodecRegistry,
   override val dbName: String
) extends MongoDb {
	
	override val conn: MongoClient = MongoClient(s"mongodb://$host:$port")
	override val db : MongoDatabase = conn.getDatabase(dbName).withCodecRegistry(codecs)
	
	override def col[T](name: String): MongoCollection[T] = db.getCollection[T](name)
	
	override def fetch[T](nReturn: Int)(implicit t : TypeTag[T]): FindObservable[T] = {
		
		val c = col[T](typeTag[T].mirror.runtimeClass(typeTag[T].tpe).getSimpleName)
		
		if (nReturn == 0) c.find[T]
		else c.find[T].skip(nReturn)
		
	}
	
	override def fetch[T](query: Bson, nReturn: Int)(implicit t : TypeTag[T]) : Seq[T] = ???
}
