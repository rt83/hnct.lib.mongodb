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

/* ------------------------------------ Embeded Models -------------------------------------- */

case class Pair[T1, T2](_1: T1, _2: T2)

/* --------------------------------------- Item model --------------------------------------- */

case class ItemM(
  modver: String,
  _id: String, 
  name: String, 
  ingrds: Seq[Pair[String, String]],
  photos: Seq[String],
  desc: String,
  feedbacks: Seq[Tuple3[String, String, Double]], // Seq{(feedbackId, userId, rankingScore)}
  foodTypes: Seq[Pair[String, String]], // Seq{(foodTypeName, foodTypeId)}
  foodCats: Seq[Pair[String, String]], // Seq{(foodCatName, foodCatId)}
  restaurant: Tuple5[String, String, Double, Double, String], // (resName, resId, (lat, lon), resAddr)
  disProgs: Seq[Pair[String, String]] // Seq{(progName, progId)}
) extends Serializable

object ItemM {
  def apply(
    name: String, 
    ingrds: Seq[Pair[String, String]],
    photos: Seq[String],
    desc: String,
    feedbacks: Seq[Tuple3[String, String, Double]], // Seq{(feedbackId, userId, rankingScore)}
    foodTypes: Seq[Pair[String, String]], // Seq{(foodTypeName, foodTypeId)}
    foodCats: Seq[Pair[String, String]], // Seq{(foodCatName, foodCatId)}
    restaurant: Tuple5[String, String, Double, Double, String], // (resName, resId, (lat, lon), resAddr)
    disProgs: Seq[Pair[String, String]] // Seq{(progName, progId)}
  ) = new ItemM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, ingrds, photos, desc, feedbacks, foodTypes, foodCats, restaurant, disProgs
  )
}

/* ----------------------------------- Ingredient model --------------------------------------- */

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

/* ----------------------------------- FoodType model --------------------------------------- */

case class FoodTypeM(
  modver: String,
  _id: String, 
  name: String, 
  desc: String
) extends Serializable

object FoodTypeM {
  def apply(
    name: String, 
    desc: String
  ) = new FoodTypeM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, desc
  )
}

/* ----------------------------------- FoodCategory model --------------------------------------- */

case class FoodCategoryM(
  modver: String,
  _id: String, 
  name: String, 
  desc: String
) extends Serializable

object FoodCategoryM {
  def apply(
    name: String, 
    desc: String
  ) = new FoodCategoryM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, desc
  )
}

/* ----------------------------------- Restaurant model --------------------------------------- */

case class RestaurantM(
  modver: String,
  _id: String, 
  name: String, 
  intro: String,
  addr: String,
  lat: Double,
  lon: Double,
  chefCook: String,
  feedbacks: Seq[Tuple3[String, String, Double]] // (feedbackId, userId, rankingScore)
) extends Serializable

object RestaurantM {
  def apply(
    name: String, 
    intro: String,
    addr: String,
    lat: Double,
    lon: Double,
    chefCook: String,
    feedbacks: Seq[Tuple3[String, String, Double]]
  ) = new RestaurantM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, intro, addr, lat, lon, chefCook, feedbacks
  )
}

/* ----------------------------------- Feedback model --------------------------------------- */

case class FeedbackM(
  modver: String,
  _id: String, 
  userId: String, 
  itemId: String,
  resId: String,
  time: String,
  comment: String,
  score: Double,
  rankDimId: String
) extends Serializable

object FeedbackM {
  def apply(
    name: String, 
    userId: String, 
    itemId: String,
    resId: String,
    time: String,
    comment: String,
    score: Double,
    rankDimId: String
  ) = new FeedbackM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    userId, itemId, resId, time, comment, score, rankDimId
  )
}

/* ----------------------------------- User model --------------------------------------- */

case class UserM(
  modver: String,
  _id: String, 
  name: String, 
  addr: String,
  accName: String,
  accPwd: String,
  roles: Seq[String],
  feedbacks: Seq[Tuple4[String, String, String, Double]] // (feedbackId, userId, restaurantId, rankingScore)
) extends Serializable

object UserM {
  def apply(
    name: String, 
    addr: String,
    accName: String,
    accPwd: String,
    roles: Seq[String], // Seq[(roleId)] 
    feedbacks: Seq[Tuple4[String, String, String, Double]] // (feedbackId, userId, restaurantId, rankingScore)
  ) = new UserM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, addr, accName, accPwd, roles, feedbacks
  )
}

/* ----------------------------------- Role model --------------------------------------- */

case class RoleM(
  modver: String,
  _id: String, 
  name: String, 
  desc: String
) extends Serializable

object RoleM {
  def apply(
    name: String, 
    desc: String
  ) = new RoleM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, desc
  )
}

/* ----------------------------------- RankingDimension model --------------------------------------- */

case class RankDimM(
  modver: String,
  _id: String, 
  name: String, 
  desc: String
) extends Serializable

object RankDimM {
  def apply(
    name: String, 
    desc: String
  ) = new RankDimM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, desc
  )
}

/* ----------------------------------- DiscountProgram model --------------------------------------- */

case class DiscountProgramM(
  modver: String,
  _id: String, 
  name: String, 
  desc: String,
  discount: Double
) extends Serializable

object DiscountProgramM {
  def apply(
    name: String, 
    desc: String,
    discount: Double
  ) = new DiscountProgramM(
    ModelBuilder.MODEL_VERSION,
    new ObjectId().toString,
    name, desc, discount
  )
}