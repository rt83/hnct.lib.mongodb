package hnct.fudivery.lib.mongodb.model

import org.bson.types.ObjectId
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.mongodb.util.JSON
import com.mongodb.DBObject

/**
 * @author tduccuong
 */
trait EntityE {
  protected val gson = new GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .setPrettyPrinting()
    .create();
  
  @Expose
  protected var _id: String = null
  
  def id_= (value: String) = _id = value
  def id = _id 
  
  def toJson() = {
    if (_id == null) _id = new ObjectId().toString() else _id
    gson.toJson(this)
  }
  
  def toDbObject() = JSON.parse(toJson()).asInstanceOf[DBObject]
}