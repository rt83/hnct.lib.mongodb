package hnct.fudivery.lib.mongodb.model

import org.bson.types.ObjectId
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.mongodb.util.JSON
import com.mongodb.DBObject
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonElement
import org.joda.time.DateTimeZone
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTime
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive

/**
 * @author tduccuong
 * Abstract entity serving as base for all Fudivery's entities
 */
trait EntityE {
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
  
  /* -------------------------- JSON Stuffs -------------------------------------- */
  
  val DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC)
  
  protected val gson = new GsonBuilder()
    .registerTypeHierarchyAdapter(classOf[Seq[Any]], new ListSerializer)
    .registerTypeHierarchyAdapter(classOf[Map[Any,Any]], new MapSerializer)
    .registerTypeHierarchyAdapter(classOf[Option[Any]], new OptionSerializer)
    .registerTypeAdapter(classOf[DateTime], new DateTimeSerializer)
    .excludeFieldsWithoutExposeAnnotation()
    .setPrettyPrinting()
    .create()
    
  class ListSerializer extends JsonSerializer[Seq[Any]] {
    override def serialize(src: Seq[Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      import scala.collection.JavaConverters._
      context.serialize(src.toList.asJava)
    }
  }
  
  class MapSerializer extends JsonSerializer[Map[Any,Any]] {
    override def serialize(src: Map[Any,Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      import scala.collection.JavaConverters._
      context.serialize(src.asJava)
    }
  }
  
  class OptionSerializer extends JsonSerializer[Option[Any]] {
    override def serialize(src: Option[Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      src match {
        case None => JsonNull.INSTANCE
        case Some(v) => context.serialize(v)
      }
    }
  }
 
  class DateTimeSerializer extends JsonSerializer[DateTime] {
    override def serialize(src: DateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      new JsonPrimitive(DATE_TIME_FORMATTER.print(src))
    }
  }
  
}

object EntityE {
  private val gson = new GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .setPrettyPrinting()
    .create()
  
  def fromDbObject(dbo: DBObject, clazz: Class[_]) = gson.fromJson(dbo.toString(), clazz)
}