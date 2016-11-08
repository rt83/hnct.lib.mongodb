package hnct.lib.mongodb.core

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import org.bson.types.{ObjectId => scalaObjectId}

object MongoUtils {
  implicit def makeAndQuery[B <: Any](criteria: Map[String, B]): DBObject = {
    val builder = MongoDBObject.newBuilder
    criteria.foreach(builder += _)
    builder.result
  }
  
  implicit def makeInQuery[B <: Any](criteria: Tuple2[String, Seq[B]]): DBObject = {
    criteria._1 $in criteria._2
  }
  
  implicit def makeSingleQuery[B <: Any](criteria: Tuple2[String, B]): DBObject = {
    MongoDBObject(criteria._1 -> criteria._2)
  }

  def mongoId = new ObjectId().toString

  def mongoIdObject = new scalaObjectId()
}