package hnct.lib.mongodb.core

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.GridFSDBFile

/**
 * This trait defines possible access operations to a MongoDB database.
 * Any MongoDB driver should extends this trait.
 */
trait MongoDb {
  /**
   * Empty the database
   */
  def emptyDb: Unit
  
  /**
   * Close the connection to DB
   */
  def closeDb: Unit

  /**
   * Get file from GridFS
   */
  def fetchFile(fileName: String): Option[GridFSDBFile]
  
  /**
   * Make a mongodb $and query given a map 
   */
  def makeQuery[B <: Any](map: Map[String, B]): DBObject
  
	/**
	 * Fetch all models of type A from db, quantity limited by nReturn
	 */
	def fetch[A <: BaseM](nReturn: Int = 0)(implicit t: Manifest[A]): Seq[A]
  
  /**
   * Fetch models whose fieldName is fieldVal
   */
  def fetchBySingleValue[A <: BaseM](fieldName: String, fieldVal: String, nReturn: Int = 0)(implicit t: Manifest[A]): Seq[A]
  
  /**
   * Given a set of string values, find models whose field contains at least one of the given values.
   */
  def fetchByMultipleValues[A <: BaseM](fieldName: String, fieldVals: Seq[String], nReturn: Int = 0)(implicit t: Manifest[A]): Seq[A]
  
  /**
   * given a MongoDB query, find models that match.
   */
  def fetchByQuery[A <: BaseM](query: DBObject, nReturn: Int = 0)(implicit t: Manifest[A]): Seq[A]
  
  
  /**
   * Insert/update a list of documents of type A in db
   */
  def persist[A <: BaseM](models: Seq[A])(implicit t: Manifest[A]): Unit
  
  /**
   * persist file to GridFS
   */
  def persistFile(fileName: String, fileNameInDb: String): Unit 
  
  def delete[A <: BaseM](models: Seq[A])(implicit t: Manifest[A]): Unit
  
  def delete[A <: BaseM](fieldName: String, fieldVals: Seq[String])(implicit t: Manifest[A]): Unit
  
  /**
   * Delete array field values in models that match one of the given arrayFieldVals
   */
  def deleteValuesInArrayField[A <: BaseM](arrayFieldName: String, arrayFieldVals: Seq[String])(implicit t: Manifest[A]): Unit
}