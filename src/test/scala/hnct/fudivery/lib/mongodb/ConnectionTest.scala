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
    Seq("1", "2", "3"),
    Seq("url1", "url2"),
    "Desc",
    Seq(
      ("fb1", "uid1", 0.2),
      ("fb1", "uid1", 0.3)
    ),
    Seq(
      ("ft1", "id1"),
      ("ft2", "id2")
    ),
    Seq(
      ("fc1", "id1"),
      ("fc2", "id2")
    ),
    ("resName", "resId", 100.0, 100.0, "resAddr"),
    Seq(
      ("p1", "id1"),
      ("p2", "id2")
    )
  )
  println(item.toJson)
//  val col = mongoDb.useCol[ItemE] 
//  col.insert(item.toDbObject)
//  
//  val query = MongoDBObject()
//  val all = mongoDb.query[ItemE](query)
//  for (doc <- all) {
//    Console.println(doc)
//    val item = ModelBuilder.fromDbObject[ItemE](doc) 
//    Console.println(item.toJson)
//  }
}