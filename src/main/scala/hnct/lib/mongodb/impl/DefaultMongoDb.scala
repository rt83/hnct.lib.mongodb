package hnct.lib.mongodb.impl

import hnct.lib.mongodb.api.MongoDb
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

/**
	* Created by Ryan on 11/6/2016.
	*/
class DefaultMongoDb(
   host : String, port : Int,
   override val codecs: CodecRegistry,
   override val dbName: String
) extends MongoDb {
	
	override val conn: MongoClient = MongoClient(s"mongodb://$host:$port")
	override val db : MongoDatabase = conn.getDatabase(dbName).withCodecRegistry(codecs)
	
	override def col[T](name: String)(implicit t : ClassTag[T]): MongoCollection[T] = db.getCollection[T](name)

	private def colName[T](implicit t : ClassTag[T]) = t.runtimeClass.getSimpleName

	override def fetch[T](nReturn: Int = 0)(implicit t : ClassTag[T]): Future[Seq[T]] = {
		val c = col[T](colName)
		
		if (nReturn == 0) c.find[T].toFuture()
		else c.find[T].limit(nReturn).toFuture()
	}
	
	override def query[T](query: Bson, nReturn: Int = 0)(implicit t : ClassTag[T]) : Future[Seq[T]] = {
		val c = col[T](colName)

		if (nReturn == 0) c.find[T](query).toFuture()
		else c.find[T](query).limit(nReturn).toFuture()
	}

	override def persist[T](models: Seq[T])(implicit t: ClassTag[T]): Future[Unit] = {
		col[T](colName).insertMany(models).toFuture().map { _ =>
			Unit	// if future completed, always return true
		}
	}

	override def persist[T](model: T)(implicit t: ClassTag[T]): Future[Unit] = {
		col[T](colName).insertOne(model).toFuture().map { _ =>
			Unit
		}
	}

	override def delete[T](query: Bson)(implicit t : ClassTag[T]) : Future[Unit] =
		col[T](colName).deleteMany(query).toFuture().map(_ => Unit)

	override def closeDb: Future[Unit] = Future.successful(conn.close())

	override def emptyDb: Future[Unit] = {
		???
	}
}
