package hnct.lib.mongodb.api

import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala._

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * Created by Ryan on 11/6/2016.
  *
  * MongoDb represent a utility class to query a mongo database. It assumes the application is using a set of models
  * and the collection has name identical to that of the model class.
  *
  */
trait MongoDb {
	
	val conn : MongoClient
	
	// the codecs used for the applications
	val codecs : CodecRegistry
	
	val dbName : String
	
	val db : MongoDatabase
	
	def col[T](name : String)(implicit t : ClassTag[T]) : MongoCollection[T]
	
	def fetch[T](nReturn : Int = 0)(implicit t : ClassTag[T]) : Future[Seq[T]]
	
	def query[T](query: Document, nReturn: Int = 0)(implicit t : ClassTag[T]) : Future[Seq[T]]

	/**
	  * Insert/update a list of documents of type A in db
	  */
	def persist[T](models: Seq[T])(implicit t: ClassTag[T]): Future[Unit]

	/**
	  * Insert/update a document of type A in db
	  */
	def persist[T](model: T)(implicit t: ClassTag[T]): Future[Unit]
	
}
