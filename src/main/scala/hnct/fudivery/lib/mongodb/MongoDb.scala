package hnct.fudivery.lib.mongodb

import com.mongodb.casbah.Imports._

class MongoDb(host: String, port: Int) {
  private val mongoClient = MongoClient(host, port)
  
  private var db: MongoDB = null
  
  def useDb(name: String) {
    db = mongoClient(name);
  }
  
  def useColl(name: String): MongoCollection = db(name)
  
  def useColl(clazz: Class[_]): MongoCollection = useColl(clazz.getSimpleName)
}
