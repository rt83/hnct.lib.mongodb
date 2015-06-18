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

/**
 * @author tduccuong
 */
object ConnectionTest extends App {
  val logger = LoggerFactory.getLogger("ConnectionTest")
  
  val mongoDb = new MongoDb("localhost", 27017)
  mongoDb.useDb("fudivery")
  
  val colTest = mongoDb.useColl(classOf[ItemE])
  
  implicit val formats = Serialization.formats(NoTypeHints)
  val abc1 = ItemE(
    new ObjectId().toString(), 
    "abc1", 
    Vector("1", "2", "3"),
    Vector("url1", "url2"),
    "Desc",
    Vector(
      ("fb1", "uid1", 0.2),
      ("fb1", "uid1", 0.3)
    )
  )
  
  colTest.insert(abc1.toDbObject)
  
  val all = colTest.find()
  for (doc <- all) {
    val item = ModelBuilder.fromDbObject[ItemE](doc) 
    Console.println(item.toJson)
  }
}