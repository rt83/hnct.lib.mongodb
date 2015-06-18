package hnct.fudivery.lib.mongodb.model

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import com.mongodb.util.JSON
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import org.json4s.DefaultFormats

/**
 * @author tduccuong
 */

trait Serializable {
  private val formats = DefaultFormats //Serialization.formats(NoTypeHints)
  
  def toJson = Serialization.writePretty(this)(formats)
  
  def toDbObject = JSON.parse(toJson).asInstanceOf[DBObject]
}

object ModelBuilder {
  // every model must be bound with a version to prevent inconsistency when loading data from db
  val MODEL_VERSION = "0.0.1"
  
  // this query will always prepended to any query in our system to ensure that only models of the current version will be loaded
  val MODEL_QUERY = MongoDBObject("modver" -> MODEL_VERSION)
  
  implicit val formats = DefaultFormats //Serialization.formats(NoTypeHints)
  
  def fromJson[T](json: String)(implicit m: Manifest[T]) = Serialization.read[T](json)
  
  def fromDbObject[T](dbo: DBObject)(implicit m: Manifest[T]) = fromJson[T](dbo.toString())
}

/* --------------------------------------- Item model --------------------------------------- */

case class ItemE(
  modver: String,
  _id: String, 
  name: String, 
  ingrds: Seq[String],
  photos: Seq[String],
  desc: String,
  feedbacks: Seq[Tuple3[String, String, Double]], // Seq{(feedbackId, userId, rankingScore)}
  foodTypes: Seq[Tuple2[String, String]], // Seq{(foodTypeName, foodTypeId)}
  foodCats: Seq[Tuple2[String, String]], // Seq{(foodCatName, foodCatId)}
  restaurant: Tuple5[String, String, Double, Double, String], // (resName, resId, (lat, lon), resAddr)
  disProgs: Seq[Tuple2[String, String]] // Seq{(progName, progId)}
) extends Serializable

object ItemE {
  def apply(
    name: String, 
    ingrds: Seq[String],
    photos: Seq[String],
    desc: String,
    feedbacks: Seq[Tuple3[String, String, Double]],
    foodTypes: Seq[Tuple2[String, String]],
    foodCats: Seq[Tuple2[String, String]],
    restaurant: Tuple5[String, String, Double, Double, String],
    disProgs: Seq[Tuple2[String, String]]
  ) = new ItemE(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, ingrds, photos, desc, feedbacks, foodTypes, foodCats, restaurant, disProgs
  )
}

/* --------------------------------------- Ingredient model --------------------------------------- */

case class IngredientE(
  modver: String,
  _id: String, 
  name: String, 
  desc: String
) extends Serializable

object IngredientE {
  def apply(
    name: String, 
    desc: String
  ) = new IngredientE(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, desc
  )
}