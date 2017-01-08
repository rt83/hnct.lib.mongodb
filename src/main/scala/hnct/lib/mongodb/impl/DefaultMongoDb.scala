package hnct.lib.mongodb.impl

import java.util.{Set => JSet}
import javax.inject.Inject

import com.google.inject.assistedinject.Assisted
import hnct.lib.mongodb.api.{MongoDb, MongoDbM}
import org.bson.codecs.configuration.{CodecRegistries, CodecRegistry}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters._

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

	override def update[IdTyp, DocTyp <: MongoDbM[IdTyp]](model: DocTyp)(implicit t: ClassTag[DocTyp]): Future[Unit] = {
		col[DocTyp](colName)
			.replaceOne(equal("_id", model._id), model)
			.toFuture().map { _ => Unit }
	}

	override def update[IdTyp, DocTyp <: MongoDbM[IdTyp]](models: Seq[DocTyp])(implicit t: ClassTag[DocTyp]): Future[Unit] = {
		models.map { model =>
			update[IdTyp, DocTyp](model)
		}.foldLeft(Future.successful()){ (acc, _) => acc }
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
