package hnct.fudivery.lib.mongodb

import com.mongodb.casbah.commons.MongoDBObject
import org.slf4j.LoggerFactory
import hnct.fudivery.lib.mongodb.model.ItemE
import com.mongodb.util.JSON
import com.mongodb.DBObject
import hnct.fudivery.lib.mongodb.model.EntityE

/**
 * @author tduccuong
 */
object ConnectionTest extends App {
  val logger = LoggerFactory.getLogger("ConnectionTest")
  
  val mongoDb = new MongoDb("localhost", 27017)
  mongoDb.useDb("fudivery")
  
  val colTest = mongoDb.useColl(classOf[ItemE])
  val item1 = new ItemE()
  item1.name = "abs"
  item1.ingredients += "igrd1" -> "6"
  Console.println(item1.toString)
  Console.println(item1.toJson)
  
  colTest.insert(item1.toDbObject())
  
  val all = colTest.find()
  for (doc <- all) {//logger.info(doc.toString())
    val item = EntityE.fromDbObject(doc, classOf[ItemE]).asInstanceOf[ItemE] 
    Console.println(item.toJson)
  }
}