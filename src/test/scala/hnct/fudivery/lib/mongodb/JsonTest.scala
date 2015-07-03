package hnct.fudivery.lib.mongodb

import org.slf4j.LoggerFactory

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import hnct.fudivery.mongodb.MongoDb
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import hnct.fudivery.mongodb.model._
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * @author tduccuong
 */
object ConnectionTest extends App {
  val mongoDb = new MongoDb("localhost", 27017, "fudivery")
  
  val all = mongoDb.query[RestaurantM]
  for (doc <- all) {
    val json = parse(doc.toString())
    Console.println(json)
//    val item = ModelBuilder.fromDbObject[FoodItemM](doc)
//    val item = json.extract[RestaurantM]
//    Console.println(item.toJson)
  }
}