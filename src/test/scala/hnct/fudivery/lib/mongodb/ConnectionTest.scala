package hnct.fudivery.lib.mongodb

import org.slf4j.LoggerFactory
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
  val logger = LoggerFactory.getLogger("ConnectionTest")
  
  val mongoDb = new MongoDb("localhost", 27017, "fudivery")
  
  val item = ItemM(
    "abc1", 
    Seq(Pair("1", "2"), Pair("4", "5")),
    Seq("url1", "url2"),
    "Desc",
    Seq(
      ("fb1", "uid1", 0.3),
      ("fb2", "uid2", 0.2)
    ),
    Seq(
      Pair("ft1", "id1"),
      Pair("ft2", "id2")
    ),
    Seq(
      Pair("fc1", "id1"),
      Pair("fc2", "id2")
    ),
    ("resName", "resId", 100.0, 100.0, "resAddr"),
    Seq(
      Pair("p1", "id1"),
      Pair("p2", "id2")
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