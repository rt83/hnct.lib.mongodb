package hnct.fudivery.lib.mongodb

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
  val db = new MongoDb("localhost", 27017, "fudivery")
  val items = db.query[FoodItemM].limit(1).map(ModelBuilder.fromDbObject[FoodItemM](_)).toIndexedSeq
  val fi = items(0)
  println(fi.toJson)
//  db.getFile(fi._id+ModelBuilder.IMG_FILENAME_SEPERATOR+fi.photos(0)).get.writeTo("/Users/tduccuong/Temp/test.jpg")
  println("completed")
}