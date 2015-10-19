package hnct.lib.mongodb.impl

import scala.reflect.runtime.universe._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import hnct.lib.mongodb.core.MongoDb
import hnct.lib.mongodb.core.MongoConn
import hnct.lib.mongodb.core.ModelBuilder
import hnct.lib.mongodb.core.BaseM

class CasbahMongo(host: String, port: Int, dbName: String) extends MongoDb {
  private val conn = new MongoConn(host, port, dbName)
  
  def emptyDb = conn.emptyDb
  
  def closeDb = conn.closeDb
  
  /* --------------------------- Fetch methods ----------------------------- */
  
  def fetch[A <: BaseM](nReturn: Int = 0)(implicit t: Manifest[A]): Seq[A] = {
    var cursor = conn.query[A]
    if (nReturn > 0) cursor = cursor.limit(nReturn)
    cursor.map(ModelBuilder.fromDbObject[A](_)).toIndexedSeq
  }
  
  def fetchBySingleValue[A <: BaseM](fieldName: String, fieldVal: String, nReturn: Int = 0)(implicit t: Manifest[A]): Seq[A] = {
    var cursor = conn.useCol[A].find(MongoDBObject(fieldName -> fieldVal))
    if (nReturn > 0) cursor = cursor.limit(nReturn)
    cursor.map(ModelBuilder.fromDbObject[A](_)).toIndexedSeq
  }
  
  def fetchByMultipleValues[A <: BaseM](fieldName: String, fieldVals: Seq[String], nReturn: Int = 0)(implicit t: Manifest[A]): Seq[A] = {
    var cursor = conn.query[A](fieldName $in fieldVals)
    if (nReturn > 0) cursor = cursor.limit(nReturn)
    cursor.map(ModelBuilder.fromDbObject[A](_)).toIndexedSeq
  }
  
  /* --------------------------- Persist methods ----------------------------- */
  
  def persist[A <: BaseM](models: Seq[A])(implicit t: Manifest[A]): Unit = {
    val col = conn.useCol[A]
    models foreach {model => col.save(model.toDbObject) }
  }
  
  
  /* --------------------------- Delete methods ----------------------------- */
  
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