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

case class FeedbackE(id: String, userId: String, rank: Double)
case class FoodTypeE(id: String, name: String)
case class FoodCategoryE(id: String, name: String)
case class RestaurantE(id: String, name: String, lat: Double, lon: Double, addr: String)
case class DiscountProgramE(id: String, name: String)

case class ItemM(
  modver: String,
  _id: String, 
  name: String, 
  ingrds: Seq[String],
  photos: Seq[String],
  desc: String,
  feedbacks: Seq[FeedbackE], // Seq{(feedbackId, userId, rankingScore)}
  foodTypes: Seq[FoodTypeE], // Seq{(foodTypeName, foodTypeId)}
  foodCats: Seq[FoodCategoryE], // Seq{(foodCatName, foodCatId)}
  restaurant: RestaurantE, // (resName, resId, (lat, lon), resAddr)
  disProgs: Seq[DiscountProgramE] // Seq{(progName, progId)}
) extends Serializable

object ItemM {
  def apply(
    name: String, 
    ingrds: Seq[String],
    photos: Seq[String],
    desc: String,
    feedbacks: Seq[FeedbackE],
	  foodTypes: Seq[FoodTypeE],
	  foodCats: Seq[FoodCategoryE],
	  restaurant: RestaurantE,
	  disProgs: Seq[DiscountProgramE]
  ) = new ItemM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, ingrds, photos, desc, feedbacks, foodTypes, foodCats, restaurant, disProgs
  )
}

/* --------------------------------------- Ingredient model --------------------------------------- */

case class IngredientM(
  modver: String,
  _id: String, 
  name: String, 
  desc: String
) extends Serializable

object IngredientM {
  def apply(
    name: String, 
    desc: String
  ) = new IngredientM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, desc
  )
}