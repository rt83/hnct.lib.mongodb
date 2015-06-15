package hnct.fudivery.lib.mongodb

import com.mongodb.casbah.commons.MongoDBObject
import org.slf4j.LoggerFactory
import hnct.fudivery.lib.mongodb.model.ItemE
import com.mongodb.util.JSON
import com.mongodb.DBObject

/**
 * @author tduccuong
 */
object ConnectionTest extends App {
  val logger = LoggerFactory.getLogger("ConnectionTest")
  
  val mongoDb = new MongoDb("localhost", 27017)
  mongoDb.useDb("fudivery")
  
  val colTest = mongoDb.useColl(classOf[ItemE])
  val item1 = new ItemE()
  item1.id = "id2";
  val item2 = new ItemE()
  
  colTest.insert(item1.toDbObject())
  colTest.insert(item2.toDbObject())
  
  val all = colTest.find()
  for (doc <- all) //logger.info(doc.toString())
    Console.println(doc.toString())
}