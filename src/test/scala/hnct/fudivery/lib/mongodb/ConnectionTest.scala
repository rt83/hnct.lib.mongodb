package hnct.fudivery.lib.mongodb

import org.slf4j.LoggerFactory
import hnct.fudivery.lib.mongodb.model.ItemE
import play.api.libs.json._
import hnct.fudivery.lib.mongodb.model.ItemE
import com.owlike.genson._
import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import hnct.fudivery.lib.mongodb.model.AbcE

/**
 * @author tduccuong
 */
object ConnectionTest extends App {
  val logger = LoggerFactory.getLogger("ConnectionTest")
  
  val mongoDb = new MongoDb("localhost", 27017)
  mongoDb.useDb("fudivery")
  
  val colTest = mongoDb.useColl(classOf[ItemE])
  
  implicit val formats = Serialization.formats(NoTypeHints)
  val abc1 = AbcE("abc1", Vector("1", "2", "3"))
  val s = Serialization.write(abc1)
  println(s)
  
  val abc2 = Serialization.read[AbcE](s)
  println(Serialization.write(abc2))
  
//  colTest.insert(item1.toDbObject())
//  
//  val all = colTest.find()
//  for (doc <- all) {
//    Console.println(doc.toString())
//    val item = EntityE.fromDbObject(doc, classOf[ItemE]).asInstanceOf[ItemE] 
//    Console.println(item.toJson)
//  }
}