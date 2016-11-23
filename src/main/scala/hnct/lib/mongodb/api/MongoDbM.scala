package hnct.lib.mongodb.api

/**
  * Created by tduccuong on 23.11.16.
  * This represents the base mongodb entity
  */
trait MongoDbM[IdType] {
  val _id: IdType
}
