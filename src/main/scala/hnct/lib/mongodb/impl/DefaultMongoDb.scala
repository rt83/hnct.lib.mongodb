package hnct.lib.mongodb.impl

import java.util.{Set => JSet}
import javax.inject.Inject

import com.google.inject.assistedinject.Assisted
import hnct.lib.mongodb.api.MongoDb
import org.bson.codecs.configuration.{CodecRegistries, CodecRegistry}
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.IndexOptions

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag
import collection.JavaConverters._

/**
	* Created by Ryan on 11/6/2016.
	*/
class DefaultMongoDb @Inject() (
   @Assisted("host") host : String, @Assisted() port : Int, @Assisted("db") override val dbName: String,
   val _codecs: JSet[CodecRegistry]   // to be injected through set binding
                               
) extends MongoDb {
	
	override val codecs : Set[CodecRegistry] = _codecs.asScala.toSet
	
	override val conn: MongoClient = MongoClient(s"mongodb://$host:$port")
	override val db : MongoDatabase = conn.getDatabase(dbName).withCodecRegistry(CodecRegistries.fromRegistries(codecs.toArray:_*))
	
	override def col[DocTyp](name: String)(implicit t : ClassTag[DocTyp]): MongoCollection[DocTyp] = db.getCollection[DocTyp](name)

	private def colName[DocTyp](implicit t : ClassTag[DocTyp]) = t.runtimeClass.getSimpleName

	def createIndex[DocTyp](key: Document, options: IndexOptions)(implicit t : ClassTag[DocTyp]): Future[String] = {
		val c = col[DocTyp](colName)
		c.createIndex(key, options).toFuture()
	}

	def createIndex[DocTyp](key: Document)(implicit t : ClassTag[DocTyp]): Future[String] = {
		val c = col[DocTyp](colName)
		c.createIndex(key).toFuture()
	}

	override def fetch[DocTyp](nReturn: Int = 0)(implicit t : ClassTag[DocTyp]): Future[Seq[DocTyp]] = {
		val c = col[DocTyp](colName)
		
		if (nReturn == 0) c.find[DocTyp].toFuture()
		else c.find[DocTyp].limit(nReturn).toFuture()
	}
	
	override def query[DocTyp](query: Bson, nReturn: Int = 0)(implicit t : ClassTag[DocTyp]) : Future[Seq[DocTyp]] = {
		val c = col[DocTyp](colName)

		if (nReturn == 0) c.find[DocTyp](query).toFuture()
		else c.find[DocTyp](query).limit(nReturn).toFuture()
	}

	override def insert[DocTyp](models: Seq[DocTyp])(implicit t: ClassTag[DocTyp]): Future[Unit] = {
		col[DocTyp](colName).insertMany(models).toFuture().map { _ =>
			Unit	// if future completed, always return true
		}
	}

	override def insert[DocTyp](model: DocTyp)(implicit t: ClassTag[DocTyp]): Future[Unit] = {
		col[DocTyp](colName).insertOne(model).toFuture().map { _ =>
			Unit
		}
	}

	override def update[DocTyp](model: DocTyp, id : String)(implicit t: ClassTag[DocTyp]): Future[Unit] = {
		col[DocTyp](colName)
			.replaceOne(equal("_id", new ObjectId(id)), model)
			.toFuture().map { _ => Unit }
	}

	override def update[DocTyp](model: DocTyp, id : ObjectId)(implicit t: ClassTag[DocTyp]): Future[Unit] = {
		col[DocTyp](colName)
			.replaceOne(equal("_id", id), model)
			.toFuture().map { _ => Unit }
	}
	
	override def updateOne[DocTyp](query : Bson, update : Bson)(implicit t: ClassTag[DocTyp]): Future[DocTyp] = {
		col[DocTyp](colName).findOneAndUpdate(query, update).toFuture()
	}

	override def delete[DocTyp](query: Bson)(implicit t : ClassTag[DocTyp]) : Future[Unit] =
		col[DocTyp](colName).deleteMany(query).toFuture().map(_ => Unit)

	override def clear[DocTyp](implicit t : ClassTag[DocTyp]) : Future[Unit] = {
		col[DocTyp](colName).drop().toFuture().map { _ =>
			db.createCollection(colName).toFuture().map { _ => Unit }
		}
	}

	override def closeDb: Future[Unit] = Future.successful(conn.close())

	override def emptyDb: Future[Unit] = {
		db.listCollectionNames().toFuture() map { colNames =>
			colNames.foreach { cn =>
				db.getCollection(cn).drop().toFuture().map(_ => Unit)
			}
		}
	}
	
	override def col[DocTyp](implicit t: ClassTag[DocTyp]): MongoCollection[DocTyp] = col[DocTyp](colName)
}
