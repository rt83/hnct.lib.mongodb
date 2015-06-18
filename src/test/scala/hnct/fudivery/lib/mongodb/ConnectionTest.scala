package hnct.fudivery.lib.mongodb

import org.slf4j.LoggerFactory
import hnct.fudivery.lib.mongodb.model.ItemE
import play.api.libs.json._
import hnct.fudivery.lib.mongodb.model.ItemE
import com.owlike.genson._
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
  
  val item = ItemE(
    "abc1", 
    Vector("1", "2", "3"),
    Vector("url1", "url2"),
    "Desc",
    Vector(
      ("fb1", "uid1", 0.2),
      ("fb1", "uid1", 0.3)
    )
  )
  val col = mongoDb.useCol[ItemE] 
  col.insert(item.toDbObject)
  
  val query = MongoDBObject()
  val all = mongoDb.query[ItemE](query)
  for (doc <- all) {
    val item = ModelBuilder.fromDbObject[ItemE](doc) 
    Console.println(item.toJson)
  }
}