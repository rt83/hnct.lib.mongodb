package hnct.fudivery.lib.mongodb

import com.mongodb.casbah.commons.MongoDBObject
import org.slf4j.LoggerFactory

/**
 * @author tduccuong
 */
object ConnectionTest extends App {
  val logger = LoggerFactory.getLogger("ConnectionTest")
  
  val mongoDb = new MongoDb("localhost", 27017)
  mongoDb.useDb("test")
  
  val colTest = mongoDb.useColl("test")
  colTest.insert(MongoDBObject("first line" -> 1))
  
  val all = colTest.find()
  for (doc <- all) logger.info(doc.toString())
//  Console.println(colTest.)
}