package hnct.fudivery.mongodb.model

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import com.mongodb.util.JSON
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

/**
 * @author tduccuong
 */

object ModelBuilder {
  // every model must be bound with a version to prevent inconsistency when loading data from db
  val MODEL_VERSION = "0.0.1"
  val IMG_FILENAME_SEPERATOR = "___"
  
  // this query will always prepended to any query in our system to ensure that only models of the current version will be loaded
  val MODEL_QUERY = MongoDBObject("modver" -> MODEL_VERSION)
  
  implicit val formats = Serialization.formats(NoTypeHints)
  
  def fromJson[T](json: String)(implicit m: Manifest[T]) = Serialization.read[T](json)
  
  def fromDbObject[T](dbo: DBObject)(implicit m: Manifest[T]) = fromJson[T](dbo.toString())
}

trait BaseM {
  private val formats = Serialization.formats(NoTypeHints)
  
  def toJson = Serialization.writePretty(this)(formats)
  
  def toDbObject = JSON.parse(toJson).asInstanceOf[DBObject]
}

/* ------------------------------------ Embeded Models -------------------------------------- */

case class Pair[T1, T2](_1: T1, _2: T2)

abstract class AbstractM(val modver: String, val _id: String)

/* --------------------------------------- Item model --------------------------------------- */

case class FoodItemM(
	name: String, 
  ingrds: Seq[Pair[String, String]], 
  photos: Seq[String],
  desc: String,
  feedbacks: Seq[Tuple3[String, String, Double]], // Seq{(feedbackId, userId, rankingScore)}
  foodTypes: Seq[Pair[String, String]], // Seq{(foodTypeName, foodTypeId)}
  foodCats: Seq[Pair[String, String]], // Seq{(foodCatName, foodCatId)}
  restaurant: Tuple5[String, String, Double, Double, String], // (resName, resId, (lat, lon), resAddr)
  discounts: Seq[Pair[String, String]] // Seq{(progName, progId)}
) extends AbstractM(ModelBuilder.MODEL_VERSION, new ObjectId().toString) with BaseM

/* ----------------------------------- Ingredient model --------------------------------------- */

case class IngredientM(
  name: String, 
  desc: String
) extends AbstractM(ModelBuilder.MODEL_VERSION, new ObjectId().toString) with BaseM

/* ----------------------------------- FoodType model --------------------------------------- */

case class FoodTypeM(
  name: String, 
  desc: String,
  photos: Seq[String]
) extends AbstractM(ModelBuilder.MODEL_VERSION, new ObjectId().toString) with BaseM

/* ----------------------------------- FoodCategory model --------------------------------------- */

case class FoodCategoryM(
  name: String, 
  desc: String,
  photos: Seq[String]
) extends AbstractM(ModelBuilder.MODEL_VERSION, new ObjectId().toString) with BaseM

/* ----------------------------------- Restaurant model --------------------------------------- */

case class RestaurantM(
  name: String, 
  intro: String,
  photos: Seq[String],
  addr: String,
  lat: Double,
  lon: Double,
  chefCook: String,
  feedbacks: Seq[Tuple3[String, String, Double]] // (feedbackId, userId, rankingScore)
) extends AbstractM(ModelBuilder.MODEL_VERSION, new ObjectId().toString) with BaseM

/* ----------------------------------- Feedback model --------------------------------------- */

case class FeedbackM(
  userId: String, 
  itemId: String,
  resId: String,
  time: String,
  comment: String,
  score: Double,
  rankDimId: String
) extends AbstractM(ModelBuilder.MODEL_VERSION, new ObjectId().toString) with BaseM

/* ----------------------------------- User model --------------------------------------- */

case class UserM(
  name: String,
  photos: Seq[String],
  addr: String,
  accName: String,
  accPwd: String,
  roles: Seq[String],
  feedbacks: Seq[Tuple4[String, String, String, Double]] // (feedbackId, userId, restaurantId, rankingScore)
) extends AbstractM(ModelBuilder.MODEL_VERSION, new ObjectId().toString) with BaseM

/* ----------------------------------- Role model --------------------------------------- */

case class UserRoleM(
  name: String, 
  desc: String
) extends AbstractM(ModelBuilder.MODEL_VERSION, new ObjectId().toString) with BaseM

/* ----------------------------------- RankingDimension model --------------------------------------- */

case class RankDimM(
  name: String, 
  desc: String
) extends AbstractM(ModelBuilder.MODEL_VERSION, new ObjectId().toString) with BaseM

/* ----------------------------------- Discount model --------------------------------------- */

case class DiscountM(
  name: String, 
  desc: String,
  photos: Seq[String],
  discount: Double
) extends AbstractM(ModelBuilder.MODEL_VERSION, new ObjectId().toString) with BaseM