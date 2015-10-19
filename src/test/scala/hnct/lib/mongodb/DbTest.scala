package hnct.lib.mongodb

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import hnct.lib.mongodb.core.MongoConn
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import hnct.lib.mongodb.impl.CasbahMongo
import hnct.lib.mongodb.core.ModelBuilder

/**
 * @author tduccuong
 */

object DbTest extends App {

  val db = new CasbahMongo("localhost", 27017, "sushibar2")
//  db.persist[CategoryM](Seq(
//      CategoryM("Cat1", ""),
//      CategoryM("Cat2", ""),
//      CategoryM("Cat3", ""),
//      CategoryM("Cat4", ""),
//      CategoryM("Cat5", ""),
//      CategoryM("Cat6", "")
//    )
//  )
  
  val cats = db.fetch[CategoryM]()
  println(Serialization.writePretty(cats)(ModelBuilder.formats))
}