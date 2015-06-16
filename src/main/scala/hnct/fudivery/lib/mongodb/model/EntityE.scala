package hnct.fudivery.lib.mongodb.model

import org.bson.types.ObjectId
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.mongodb.util.JSON
import com.mongodb.DBObject

/**
 * @author tduccuong
 * Abstract entity serving as base for all Fudivery's entities
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
  
  def fromJson(json: String) = gson.fromJson(json, this.getClass) 
  
  def toDbObject() = JSON.parse(toJson()).asInstanceOf[DBObject]
  
  def fromDbObject(dbo: DBObject) = fromJson(dbo.toString()) 
}

object EntityE {
  private val gson = new GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .setPrettyPrinting()
    .create();
  
  def fromDbObject(dbo: DBObject, clazz: Class[_]) = gson.fromJson(dbo.toString(), clazz)
}