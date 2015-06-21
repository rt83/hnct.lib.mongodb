package hnct.fudivery.lib.mongodb

import org.slf4j.LoggerFactory
import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import hnct.fudivery.lib.mongodb.model._
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject

/**
 * @author tduccuong
 */
object ConnectionTest extends App {
  val logger = LoggerFactory.getLogger("ConnectionTest")
  
  val mongoDb = new MongoDb("localhost", 27017, "fudivery")
  
  val item = ItemM(
    "abc1", 
    Seq("1", "2", "3"),
    Seq("url1", "url2"),
    "Desc",
    Seq(
      FeedbackE("fb1", "uid1", 0.3),
      FeedbackE("fb2", "uid2", 0.2)
    ),
    Seq(
      FoodTypeE("ft1", "id1"),
      FoodTypeE("ft2", "id2")
    ),
    Seq(
      FoodCategoryE("fc1", "id1"),
      FoodCategoryE("fc2", "id2")
    ),
    RestaurantE("resName", "resId", 100.0, 100.0, "resAddr"),
    Seq(
      DiscountProgramE("p1", "id1"),
      DiscountProgramE("p2", "id2")
    )
  )
//  println(item.toJson)
  val col = mongoDb.useCol[ItemM] 
  col.insert(item.toDbObject)
  
  val query = MongoDBObject()
  val all = mongoDb.query[ItemM](query)
  for (doc <- all) {
//    Console.println(doc)
    val item = ModelBuilder.fromDbObject[ItemM](doc) 
    Console.println(item.toJson)
  }
}