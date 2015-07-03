package hnct.fudivery.lib.mongodb

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import hnct.fudivery.mongodb.MongoDb
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import hnct.fudivery.mongodb.model._

/**
 * @author tduccuong
 */
object ConnectionTest extends App {
  val db = new MongoDb("localhost", 27017, "fudivery")
  
//  val restaurants = db.query[RestaurantM]
//      .map(ModelBuilder.fromDbObject[RestaurantM](_))
//      .toIndexedSeq
//  val json = ModelBuilder.listToJson(restaurants)
//  println(json)
  
  val foodItems = db.query[FoodItemM]
      .limit(2)
      .map(ModelBuilder.fromDbObject[FoodItemM](_))
      .toIndexedSeq
    val restaurants = db.query[RestaurantM]
      .limit(2)
      .map(ModelBuilder.fromDbObject[RestaurantM](_))
      .toIndexedSeq
    
    /* Serialize data */
    implicit val formats = Serialization.formats(NoTypeHints)
    case class Result(foodItems: Seq[FoodItemM], restaurants: Seq[RestaurantM])
    val result = Serialization.writePretty(Result(foodItems, restaurants))
    println(result)
}