package hnct.fudivery.lib.mongodb

import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL.WithDouble._
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
    val result = ("foodItems" -> ModelBuilder.listToJson(foodItems)) ~
                 ("restaurants" -> ModelBuilder.listToJson(restaurants))
    println(compact(render(result)))
}