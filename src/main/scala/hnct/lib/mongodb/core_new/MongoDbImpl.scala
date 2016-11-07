package hnct.lib.mongodb.core_new

import hnct.lib.mongodb.api.MongoDb
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{ Document, MongoClient, MongoCollection, MongoDatabase}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

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
	
	override def col[T](name: String)(implicit t : ClassTag[T]): MongoCollection[T] = db.getCollection[T](name)

	private def colName[T](implicit t : ClassTag[T]) = t.runtimeClass.getSimpleName

	override def fetch[T](nReturn: Int)(implicit t : ClassTag[T]): Future[Seq[T]] = {
		
		val c = col[T](colName)
		
		if (nReturn == 0) c.find[T].toFuture()
		else c.find[T].limit(nReturn).toFuture()

	}
	
	override def fetch[T](query: Document, nReturn: Int)(implicit t : ClassTag[T]) : Future[Seq[T]] = {
		val c = col[T](colName)

		if (nReturn == 0) c.find[T](query).toFuture()
		else c.find[T](query).limit(nReturn).toFuture()
	}

	/**
	  * Insert/update a list of documents of type A in db
	  */
	override def persist[T](models: Seq[T])(implicit t: ClassTag[T]): Future[Unit] = {
		col[T](colName).insertMany(models).toFuture().map { _ =>
			Unit	// if future completed, always return true
		}
	}

	/**
	  * Insert/update a document of type A in db
	  */
	override def persist[T](model: T)(implicit t: ClassTag[T]): Future[Unit] = {
		col[T](colName).insertOne(model).toFuture().map { _ =>
			Unit
		}
	}
}
