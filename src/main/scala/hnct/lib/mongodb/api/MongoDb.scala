package hnct.lib.mongodb.api

import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala._
import org.mongodb.scala.bson.conversions.Bson

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

	/**
		* Get collection by name from db. Supposed to be used by
		* implementation class of this trait
		* @param name
		* @param t
		* @tparam T
		* @return
		*/
	protected def col[T](name : String)(implicit t : ClassTag[T]) : MongoCollection[T]

	/**
		* Fetch all models of type T in db limited to nReturn number of models.
		* @param nReturn
		* @param t
		* @tparam T
		* @return
		*/
	def fetch[T](nReturn : Int = 0)(implicit t : ClassTag[T]) : Future[Seq[T]]

	/**
		* Fetch models of type T from db given a query
		* @param query
		* @param nReturn
		* @param t
		* @tparam T
		* @return
		*/
	def query[T](query: Bson, nReturn: Int = 0)(implicit t : ClassTag[T]) : Future[Seq[T]]

	/**
		* Bulk save/update a list of models in db
		* @param models
		* @param t
		* @tparam T
		* @return
		*/
	def persist[T](models: Seq[T])(implicit t: ClassTag[T]): Future[Unit]

	/**
		* Save/update a model in db.
		* @param model
		* @param t
		* @tparam T
		* @return
		*/
	def persist[T](model: T)(implicit t: ClassTag[T]): Future[Unit]

	/**
		* Delete models in db given criteria.
		* @param conds
		* @param t
		* @tparam T
		* @return
		*/
	def delete[T](conds: Bson)(implicit t: ClassTag[T]): Future[Unit]

	/**
		* Clear the collection of type T
		* @param t
		* @tparam T
		* @return
		*/
	def clear[T](implicit t : ClassTag[T]) : Future[Unit]

	/**
		* Close the connection to the db
		* @return
		*/
	def closeDb: Future[Unit]

	/**
		* Empty the database
		* @return
		*/
	def emptyDb: Future[Unit]
	
}
