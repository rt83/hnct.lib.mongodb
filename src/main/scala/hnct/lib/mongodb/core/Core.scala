package hnct.lib.mongodb.core

import org.json4s.jackson.Serialization
import com.mongodb.util.JSON
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.github.nscala_time.time.Imports._
import org.json4s.FieldSerializer
import org.json4s.FieldSerializer.ignore
import org.json4s.DefaultFormats

/**
 * @author tduccuong
 */

object ModelBuilder {
  // every model must be bound with a version to prevent inconsistency when loading data from db
  val MODEL_VERSION = "0.0.1"
  val IMG_FILENAME_SEPERATOR = "___"
  
  // this query will always prepended to any query in our system to ensure that only models of the current version will be loaded
  val MODEL_QUERY = MongoDBObject("ver" -> MODEL_VERSION)
  
  val formats = DefaultFormats + FieldSerializer[BaseM](ignore("id")) + FieldSerializer[BaseM](ignore("creTime"))
  
  def fromJson[A <: BaseM](json: String)(implicit mf: Manifest[A]) = {
    Serialization.read[A](json)(formats, mf)
  }
  
  def fromDbObject[A <: BaseM](dbo: DBObject)(implicit mf: Manifest[A]) = {
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

abstract class BaseM(id: Option[String], creTime: Option[String] = None) extends Serializable {
  /**
   * Version of this model
   */
  val ver = ModelBuilder.MODEL_VERSION
  
  /**
   * The creation time of this object
   */
  val cre: String = creTime match {
    case None => LocalDateTime.now.toString()
    case Some(time) => time
  }
  
  /**
   * The ID of this object. This ID should be unique world-wide
   */
  val _id: String = id match {
    case None => new ObjectId().toString
    case Some(value) => value 
  }
}