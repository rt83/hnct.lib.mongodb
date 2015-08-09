package hnct.fudivery.mongodb.model

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

/* ------------------------------------ Embeded Models -------------------------------------- */

case class Pair[T1, T2](_1: T1, _2: T2)

/* ------------------------------------- Food Dimension ------------------------------------- */

case class DimensionM (
  _id: String,
  modver: String,
  created: String,
  name: String,
  desc: String,
  priority: Int,
  orderCount: Long,
  viewCount: Long
) extends BaseM

object DimensionM {
  def apply(
    name: String, 
    desc: String, 
    priority: Int, 
    orderCount: Long, 
    viewCount: Long
  ) = new DimensionM (
 		new ObjectId().toString, ModelBuilder.MODEL_VERSION, LocalDateTime.now.toString(), name, desc, priority, orderCount, viewCount    
  )
}

/* ------------------------------------- Food Dimension Values ------------------------------------- */

case class DimValM (
  _id: String,
  modver: String,
  created: String,
  name: String,
  dims: Seq[String], // IDs of the dimensions that this dimval belongs to
  orderCount: Long,
  viewCount: Long
) extends BaseM

object DimValM {
  def apply(
    name: String, 
    dims: Seq[String], 
    orderCount: Long, 
    viewCount: Long
  ) = new DimValM(
    new ObjectId().toString, 
    ModelBuilder.MODEL_VERSION,
    LocalDateTime.now.toString(),
    name, dims, orderCount, viewCount    
  )
}

/* ------------------------------------- Food Keyword ------------------------------------- */

case class KeywordM (
  _id: String,
  modver: String,
  created: String,
  name: String,
  dimvals: String, // IDs of the dimvals that this keyword belongs to
  wordCount: Int // number of single words of this keyword token, index on this field for quick search 
) extends BaseM

object KeywordM {
  def apply(
    name: String, 
    dimvals: String, 
    wordCount: Int 
  ) = new KeywordM(
    new ObjectId().toString,
    ModelBuilder.MODEL_VERSION, LocalDateTime.now.toString(), name, dimvals, wordCount    
  )
}

/* ------------------------------------- Mean Token ------------------------------------- */

case class NegativeWordM (
  _id: String,
  modver: String,
  created: String,
  name: String
) extends BaseM

object NegativeWordM {
  def apply(
    name: String
  ) = new NegativeWordM(
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
  rankDim: RankDim.Value,
  rank: Double,
  comment: String
)

case class RestaurantE (
  id: String,
  name: String,
  loc: Pair[Double, Double],
  addr: String,
  cat: String // the food category provided by the restaurant that this food item belongs to
)

case class FoodItemM (
  _id: String,
  modver: String,
  created: String,
	name: String, 
	desc: String,
  photos: Seq[String],
  dimvals: Seq[String], // list of dimvals' ID that this food item is attached to
  feedbacks: Seq[FeedbackE], // Seq{(feedbackId, userId, rankingScore)}
  restaurant: RestaurantE,
  disProgs: Seq[String] // list of discount programs
) extends BaseM

object FoodItemM {
  def apply(
    name: String, 
    desc: String,
    photos: Seq[String],
    dimvals: Seq[String],
    feedbacks: Seq[FeedbackE],
    restaurant: RestaurantE,
    disProgs: Seq[String]
  ) = new FoodItemM(
    new ObjectId().toString,
    ModelBuilder.MODEL_VERSION,
    LocalDateTime.now.toString(),
    name, desc, photos, dimvals, feedbacks,restaurant, disProgs
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
  val PRICE = Value("price")
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
  rankDim: RankDim.Value
) extends BaseM

object FeedbackM {
  def apply(
    userId: String, 
    itemId: String,
    resId: String,
    time: String,
    comment: String,
    score: Double,
    rankDim: RankDim.Value
  ) = new FeedbackM(
    new ObjectId().toString,
    ModelBuilder.MODEL_VERSION,
    LocalDateTime.now.toString(),
    userId, itemId, resId, time, comment, score, rankDim
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