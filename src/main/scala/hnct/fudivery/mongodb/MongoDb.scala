package hnct.fudivery.mongodb

import com.mongodb.casbah.Imports._
import hnct.fudivery.lib.mongodb.model.ModelBuilder

class MongoDb(host: String, port: Int, dbName: String) {
  private val mongoClient = MongoClient(host, port)
  
  private val db = mongoClient(dbName)
  private var cols = Map[String, MongoCollection]()
  
  def useCol(name: String) = {
    if (cols.contains(name)) cols(name)
    else {
      val col = db(name)
      cols = cols + (name -> col)
      col
    }
  }
  
  def useCol[T](implicit tag: reflect.ClassTag[T]): MongoCollection = useCol(tag.runtimeClass.getSimpleName)
  
  /**
   * Ensure that the will-be-loaded models's is always consistent with the current model version
   */
  def query(colName: String)(q: DBObject) = {
    val queryList = List(ModelBuilder.MODEL_QUERY, q)
    useCol(colName).find(MongoDBObject("$and" -> queryList))
  }
  
  /**
   * Ensure that the will-be-loaded models's is always consistent with the current model version
   */
  def query[T](q: DBObject)(implicit tag: reflect.ClassTag[T]) = {
    val queryList = List(ModelBuilder.MODEL_QUERY, q)
    useCol(tag.runtimeClass.getSimpleName).find(MongoDBObject("$and" -> queryList)) 
  }
}
