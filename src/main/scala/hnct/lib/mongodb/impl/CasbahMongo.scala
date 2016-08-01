package hnct.lib.mongodb.impl

import scala.reflect.runtime.universe._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import hnct.lib.mongodb.core.MongoDb
import hnct.lib.mongodb.core.MongoConn
import hnct.lib.mongodb.core.ModelBuilder
import hnct.lib.mongodb.core.BaseM
import com.mongodb.casbah.gridfs.GridFSDBFile

class CasbahMongo(host: String, port: Int, dbName: String) extends MongoDb {
  private val conn = new MongoConn(host, port, dbName)
  
  def emptyDb = conn.emptyDb
  
  def closeDb = conn.closeDb
  
  /* --------------------------- Fetch methods ----------------------------- */
  
  def fetchFile(fileName: String): Option[GridFSDBFile] = conn.getFile(fileName)
  
  def fetch[A <: BaseM](nReturn: Int = 0)(implicit t: Manifest[A]): Seq[A] = {
    var cursor = conn.query[A]
    if (nReturn > 0) cursor = cursor.limit(nReturn)
    cursor.map(ModelBuilder.fromDbObject[A](_)).toIndexedSeq
  }
  
  def fetchByQuery[A <: BaseM](query: DBObject, nReturn: Int = 0)(implicit t: Manifest[A]): Seq[A] = {
    var cursor = conn.query[A](query)
    if (nReturn > 0) cursor = cursor.limit(nReturn)
    cursor.map(ModelBuilder.fromDbObject[A](_)).toIndexedSeq
  }
  
  /* --------------------------- Persist methods ----------------------------- */
  
  def persist[A <: BaseM](models: Seq[A])(implicit t: Manifest[A]): Unit = {
    val col = conn.useCol[A]
    models foreach {model => col.save(model.toDbObject) }
  }
  
  def persist[A <: BaseM](model: A)(implicit t: Manifest[A]): Unit = {
    val col = conn.useCol[A]
    col.save(model.toDbObject)
  }
  
  def persistFile(fileName: String, fileNameInDb: String): Unit = conn.saveFile(fileName, fileNameInDb)
  
  /* --------------------------- Delete methods ----------------------------- */
  
  def emptyColl[A <: BaseM]()(implicit t: Manifest[A]): Unit = conn.useCol[A].remove(MongoDBObject.empty)
  
  def delete[A <: BaseM](models: Seq[A])(implicit t: Manifest[A]): Unit = {
    val col = conn.useCol[A]
    models foreach {model => col.remove(model.toDbObject) }
  }
  
  def delete[A <: BaseM](fieldName: String, fieldVals: Seq[String])(implicit t: Manifest[A]): Unit = {
    val col = conn.useCol[A]
    col.remove(fieldName $in fieldVals)
  }
  
  def deleteValuesInArrayField[A <: BaseM](arrayFieldName: String, arrayFieldVals: Seq[String])(implicit t: Manifest[A]): Unit = {
    conn.useCol[A].update(
      MongoDBObject(), 
      $pull (arrayFieldName $in arrayFieldVals), 
      true, 
      true
    )
  }

}