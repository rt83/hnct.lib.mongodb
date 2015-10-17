package hnct.lib.mongodb.core

import com.mongodb.casbah.Imports._
import scala.reflect.runtime.universe._

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
	 * Fetch all models of type A from db, quantity limited by nReturn
	 */
	def fetch[A <: BaseM](nReturn: Int = 0)(implicit t: TypeTag[A]): Seq[A]
  
  /**
   * Fetch models whose fieldName is fieldVal
   */
  def fetchBySingleValue[A <: BaseM](fieldName: String, fieldVal: String, nReturn: Int = 0)(implicit t: TypeTag[A]): Seq[A]
  
  /**
   * Given a set of string values, find models whose field contains at least one of the given values.
   */
  def fetchByMultipleValues[A <: BaseM](fieldName: String, fieldVals: Seq[String], nReturn: Int = 0)(implicit t: TypeTag[A]): Seq[A]
  
  
  
  /**
   * Insert/update a list of documents of type A in db
   */
  def persist[A <: BaseM](models: Seq[A])(implicit t: TypeTag[A]): Unit
  
  
  
  def delete[A <: BaseM](models: Seq[A])(implicit t: TypeTag[A]): Unit
  
  def delete[A <: BaseM](fieldName: String, fieldVals: Seq[String])(implicit t: TypeTag[A]): Unit
  
  /**
   * Delete array field values in models that match one of the given arrayFieldVals
   */
  def deleteValuesInArrayField[A <: BaseM](arrayFieldName: String, arrayFieldVals: Seq[String])(implicit t: TypeTag[A]): Unit
}