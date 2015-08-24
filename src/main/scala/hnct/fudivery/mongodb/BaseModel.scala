package hnct.fudivery.mongodb

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import com.mongodb.util.JSON
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.github.nscala_time.time.Imports._
import reflect.runtime.universe._
import scala.reflect.ClassTag

/**
 * @author tduccuong
 */

object ModelBuilder {
  // every model must be bound with a version to prevent inconsistency when loading data from db
  val MODEL_VERSION = "0.0.1"
  val IMG_FILENAME_SEPERATOR = "___"
  
  // this query will always prepended to any query in our system to ensure that only models of the current version will be loaded
  val MODEL_QUERY = MongoDBObject("modver" -> MODEL_VERSION)
  
  private val formats = Serialization.formats(NoTypeHints)
  
  private def manifestOf[A:TypeTag] = {
    val t = typeTag[A]
    implicit val cl = ClassTag[A](t.mirror.runtimeClass(t.tpe))
    manifest[A]
  }
  
  def fromJson[A](json: String)(implicit t: TypeTag[A]) = {
    val mf = manifestOf[A]
    Serialization.read[A](json)(formats, mf)
  }
  
  def fromDbObject[A](dbo: DBObject)(implicit t: TypeTag[A]) = {
    fromJson[A](dbo.toString())
  }
}

trait BaseM {
  private val formats = Serialization.formats(NoTypeHints)
  
  def toJson = Serialization.writePretty(this)(formats)
  
  def toDbObject = JSON.parse(toJson).asInstanceOf[DBObject]
}