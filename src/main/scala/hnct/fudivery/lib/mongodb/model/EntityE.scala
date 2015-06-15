package hnct.fudivery.lib.mongodb.model

/**
 * @author tduccuong
 */
trait EntityE {
  val _id: String
  
  def toJson(): String
}