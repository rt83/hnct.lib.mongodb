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

abstract class AbstractM {
  val modver: String = ModelBuilder.MODEL_VERSION
  val _id: String =  new ObjectId().toString
  val created: String = LocalDateTime.now.toString()
}

/* ------------------------------------- Food Dimension ------------------------------------- */

case class FoodDimensionM (
  name: String,
  desc: String,
  priority: Int,
  orderCount: Long,
  viewCount: Long
) extends AbstractM with BaseM

case class FoodDimensionKeyword (
  name: String,
  foodDimId: String, // ID of the FoodDimension that this keyword belongs to
  relateds: Seq[String], // array of IDs of related FoodDimensionKeywords
  orderCount: Long,
  viewCount: Long,
  msFunc: Seq[Pair[Double, Double]] // membership function of this keyword for future fuzzy search
) extends AbstractM with BaseM

/* --------------------------------------- Item model --------------------------------------- */

case class FoodItemM (
	name: String, 
	desc: String,
  photos: Seq[String],
  feedbacks: Seq[Tuple3[String, String, Double]], // Seq{(feedbackId, userId, rankingScore)}
  restaurant: Tuple5[String, String, Double, Double, String], // (resName, resId, (lat, lon), resAddr)
  discounts: Seq[Pair[String, String]] // Seq{(progName, progId)}
) extends AbstractM with BaseM

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
) extends AbstractM with BaseM

/* ----------------------------------- Feedback model --------------------------------------- */

object RankingDimension extends Enumeration {
  val SATISFACTION = Value("satisfaction")
  val TASTE = Value("taste")
  val HEALTHINESS = Value("healthiness")
}

case class FeedbackM(
  userId: String, 
  itemId: String,
  resId: String,
  time: String,
  comment: String,
  score: Double,
  rankDimId: RankingDimension.Value
) extends AbstractM with BaseM

/* ----------------------------------- User model --------------------------------------- */

object UserRole extends Enumeration {
  val ADMIN = Value("admin")
  val USER = Value("user")
  val CUSTOMER = Value("customer") // customer refers to restaurant or someone who can cook and provide food for our system.
}

case class UserM(
  name: String,
  photos: Seq[String],
  addr: String,
  accName: String,
  accPwd: String,
  roles: Seq[UserRole.Value]
) extends AbstractM with BaseM

/* ----------------------------------- Discount model --------------------------------------- */

case class DiscountM(
  name: String, 
  desc: String,
  photos: Seq[String],
  discount: Double
) extends AbstractM with BaseM