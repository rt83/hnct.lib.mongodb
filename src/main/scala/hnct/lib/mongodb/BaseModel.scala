package hnct.lib.mongodb

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import com.mongodb.util.JSON
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.github.nscala_time.time.Imports._
import scala.reflect.runtime.universe._
import scala.reflect.ClassTag
import org.json4s.FieldSerializer
import org.json4s.FieldSerializer.ignore
import org.json4s.DefaultFormats
import org.json4s.Formats

/**
 * @author tduccuong
 */

object ModelBuilder {
  // every model must be bound with a version to prevent inconsistency when loading data from db
  val MODEL_VERSION = "0.0.1"
  val IMG_FILENAME_SEPERATOR = "___"
  
  // this query will always prepended to any query in our system to ensure that only models of the current version will be loaded
  val MODEL_QUERY = MongoDBObject("ver" -> MODEL_VERSION)
  
  val formats = DefaultFormats + FieldSerializer[BaseM](ignore("id"))
  
  private def manifestOf[A:TypeTag] = {
    val t = typeTag[A]
    implicit val cl = ClassTag[A](t.mirror.runtimeClass(t.tpe))
    manifest[A]
  }
  
  def fromJson[A <: BaseM](json: String)(implicit t: TypeTag[A]) = {
    val mf = manifestOf[A]
    Serialization.read[A](json)(formats, mf)
  }
  
  def fromDbObject[A <: BaseM](dbo: DBObject)(implicit t: TypeTag[A]) = {
    fromJson[A](dbo.toString())
  }
}

trait Serializable {
  /**
   * Serialize this object to JSON
   */
  def toJson = Serialization.writePretty(this)(ModelBuilder.formats) 
  
  /**
   * Serialize this object to DB entity
   */
  def toDbObject = JSON.parse(toJson).asInstanceOf[DBObject]
}

abstract class BaseM(id: Option[String]) extends Serializable {
  /**
   * Version of this model
   */
  val ver = ModelBuilder.MODEL_VERSION
  
  /**
   * The creation time of this object
   */
  val cre = LocalDateTime.now.toString()
  
  /**
   * The ID of this object. This ID should be unique world-wide
   */
  val _id: String = id match {
    case None => new ObjectId().toString
    case Some(value) => value 
  }
}