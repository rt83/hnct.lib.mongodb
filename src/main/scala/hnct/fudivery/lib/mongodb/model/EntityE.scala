package hnct.fudivery.lib.mongodb.model

import org.bson.types.ObjectId
import play.api.libs.json._

/**
 * @author tduccuong
 * Abstract entity serving as base for all Fudivery's entities
 */
trait EntityE {
  protected var _id: String = null
  
  def id_= (value: String) = _id = value
  def id = _id 
  
//  protected def _toJson() = {
//    if (_id == null) _id = new ObjectId().toString() else _id
//    Json.obj(
//      "_id" -> _id
//    )
//  }
//  
//  def toJson(): String
  
  /* HELPERS */
  
}