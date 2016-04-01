package hnct.lib.mongodb

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import hnct.lib.mongodb.core.MongoConn
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import hnct.lib.mongodb.impl.CasbahMongo
import hnct.lib.mongodb.core.MongoUtils._
import hnct.lib.mongodb.core.ModelBuilder

/**
 * @author tduccuong
 */

object DbTest extends App {

  val db = new CasbahMongo("localhost", 27017, "test")
  db.emptyDb
  db.persist[CategoryM](Seq(
      CategoryM("Cat1", ""),
      CategoryM("Cat2", ""),
      CategoryM("Cat3", "abc"),
      CategoryM("Cat4", ""),
      CategoryM("Cat5", "def"),
      CategoryM("Cat6", "")
    )
  )
  
//  val cats = db.fetch[CategoryM]()
//  println(Serialization.writePretty(cats)(ModelBuilder.formats))
  
  val cat1s = db.fetchByQuery[CategoryM]("name" -> Seq("Cat3","Cat6"))
  println(cat1s)
  val cat2s = db.fetchByQuery[CategoryM](Map("name" -> "Cat3", "desc" -> "abc"))
  println(cat2s)
}