package hnct.fudivery.mongodb.model

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import com.mongodb.util.JSON
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.github.nscala_time.time.Imports._

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

/* ------------------------------------- Food Dimension ------------------------------------- */

case class FoodDimensionM (
  _id: String,
  modver: String,
  created: String,
  name: String,
  desc: String,
  priority: Int,
  orderCount: Long,
  viewCount: Long
) extends BaseM

object FoodDimensionM {
  def apply(
    name: String,
    desc: String,
    priority: Int,
    orderCount: Long,
    viewCount: Long
  ) = new FoodDimensionM (
 		new ObjectId().toString,
    ModelBuilder.MODEL_VERSION,
    LocalDateTime.now.toString(),
    name, desc, priority, orderCount, viewCount    
  )
}

/* ------------------------------------- Food Keyword ------------------------------------- */

case class FoodKeywordM (
  _id: String,
  modver: String,
  created: String,
  name: String,
  foodDimId: String, // ID of the FoodDimension that this keyword belongs to
  orderCount: Long,
  viewCount: Long,
  tokens: Seq[String] // tokens of this keywords
) extends BaseM

object FoodKeywordM {
  def apply(
    name: String, 
    foodDimId: String, 
    orderCount: Long, 
    viewCount: Long, 
    tokens: Seq[String]
  ) = new FoodKeywordM(
    new ObjectId().toString, 
    ModelBuilder.MODEL_VERSION,    
    LocalDateTime.now.toString(),    
    name, foodDimId, orderCount, viewCount, tokens    
  )
}

/* ------------------------------------- Meaningful Keyword Token ------------------------------------- */

case class MeaningfulTokenM (
  _id: String,
  modver: String,
  created: String,
  name: String
) extends BaseM

object MeaningfulTokenM {
  def apply(
    name: String
  ) = new MeaningfulTokenM(
    new ObjectId().toString,    
    ModelBuilder.MODEL_VERSION,    
    LocalDateTime.now.toString(),    
    name    
  )
}

/* --------------------------------------- Item model --------------------------------------- */

case class FeedbackE (
  id: String,
  userId: String,
  rank: Double,
  comment: String
)

case class RestaurantE (
  id: String,
  name: String,
  loc: Pair[Double, Double],
  addr: String
)

case class FoodItemM (
  _id: String,
  modver: String,
  created: String,
	name: String, 
	desc: String,
  photos: Seq[String],
  keywords: Seq[String], // list of keyword IDs
  feedbacks: Seq[FeedbackE], // Seq{(feedbackId, userId, rankingScore)}
  restaurant: RestaurantE,
  disProgs: Seq[String] // list of discount programs
) extends BaseM

object FoodItemM {
  def apply(
    name: String, 
    desc: String,
    photos: Seq[String],
    keywords: Seq[String],
    feedbacks: Seq[FeedbackE],
    restaurant: RestaurantE,
    disProgs: Seq[String]
  ) = new FoodItemM(
    new ObjectId().toString,
    ModelBuilder.MODEL_VERSION,
    LocalDateTime.now.toString(),
    name, desc, photos, keywords, feedbacks,restaurant, disProgs
  )
}

/* ----------------------------------- Restaurant model --------------------------------------- */

case class RestaurantM(
  _id: String,
  modver: String,
  created: String,
  name: String, 
  intro: String,
  photos: Seq[String],
  addr: String,
  lat: Double,
  lon: Double,
  chefCook: String,
  feedbacks: Seq[FeedbackE]
) extends BaseM

object RestaurantM {
  def apply(
    name: String, 
    intro: String,
    photos: Seq[String],
    addr: String,
    lat: Double,
    lon: Double,
    chefCook: String,
    feedbacks: Seq[FeedbackE]
  ) = new RestaurantM(
    new ObjectId().toString,
    ModelBuilder.MODEL_VERSION,
    LocalDateTime.now.toString(),
    name, intro, photos, addr, lat,lon, chefCook,feedbacks
  )
}

/* ----------------------------------- Feedback model --------------------------------------- */

object RankDim extends Enumeration {
  val SATISFACTION = Value("satisfaction")
  val TASTE = Value("taste")
  val HEALTHINESS = Value("healthiness")
}

case class FeedbackM(
  _id: String,
  modver: String,
  created: String,
  userId: String, 
  itemId: String,
  resId: String,
  time: String,
  comment: String,
  score: Double,
  rankDimId: RankDim.Value
) extends BaseM

object FeedbackM {
  def apply(
    userId: String, 
    itemId: String,
    resId: String,
    time: String,
    comment: String,
    score: Double,
    rankDimId: RankDim.Value
  ) = new FeedbackM(
    new ObjectId().toString,
    ModelBuilder.MODEL_VERSION,
    LocalDateTime.now.toString(),
    userId, itemId, resId, time, comment,score, rankDimId
  )
}

/* ----------------------------------- User model --------------------------------------- */

object UserRole extends Enumeration {
  val ADMIN = Value("admin")
  val USER = Value("user")
  val CUSTOMER = Value("customer") // customer refers to restaurant or someone who can cook and provide food for our system.
}

case class UserM(
  _id: String,
  modver: String,
  created: String,
  name: String,
  photos: Seq[String],
  addr: String,
  accName: String,
  accPwd: String,
  roles: Seq[UserRole.Value]
) extends BaseM

object UserM {
  def apply(
    name: String,
    photos: Seq[String],
    addr: String,
    accName: String,
    accPwd: String,
    roles: Seq[UserRole.Value]
  ) = new UserM(
    new ObjectId().toString,
    ModelBuilder.MODEL_VERSION,
    LocalDateTime.now.toString(),
    name, photos, addr, accName, accPwd, roles
  )
}

/* ----------------------------------- Discount model --------------------------------------- */

case class DiscountM(
  _id: String,
  modver: String,
  created: String,
  name: String, 
  desc: String,
  photos: Seq[String],
  discount: Double
) extends BaseM

object DiscountM {
  def apply(
    name: String, 
    desc: String,
    photos: Seq[String],
    discount: Double
  ) = new DiscountM(
    new ObjectId().toString,
    ModelBuilder.MODEL_VERSION,
    LocalDateTime.now.toString(),
    name, desc, photos, discount
  )
}