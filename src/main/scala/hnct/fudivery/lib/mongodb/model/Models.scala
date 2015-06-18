package hnct.fudivery.lib.mongodb.model

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import com.mongodb.util.JSON
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

/**
 * @author tduccuong
 */

trait Serializable {
  private val formats = Serialization.formats(NoTypeHints)
  
  def toJson = Serialization.writePretty(this)(formats)
  
  def toDbObject = JSON.parse(toJson).asInstanceOf[DBObject]
}

object ModelBuilder {
  // every model must be bound with a version to prevent inconsistency when loading data from db
  val MODEL_VERSION = "0.0.1"
  
  // this query will always prepended to any query in our system to ensure that only models of the current version will be loaded
  val MODEL_QUERY = MongoDBObject("modver" -> MODEL_VERSION)
  
  implicit val formats = Serialization.formats(NoTypeHints)
  
  def fromJson[T](json: String)(implicit m: Manifest[T]) = Serialization.read[T](json)
  
  def fromDbObject[T](dbo: DBObject)(implicit m: Manifest[T]) = fromJson[T](dbo.toString())
}

case class ItemE(
  modver: String,
  _id: String, 
  name: String, 
  ingrds: Seq[String],
  photos: Seq[String],
  desc: String,
  feedbacks: Seq[Tuple3[String, String, Double]]
) extends Serializable

object ItemE {
  def apply(
    name: String, 
    ingrds: Seq[String],
    photos: Seq[String],
    desc: String,
    feedbacks: Seq[Tuple3[String, String, Double]]
  ) = new ItemE(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, ingrds, photos, desc, feedbacks
  )
}