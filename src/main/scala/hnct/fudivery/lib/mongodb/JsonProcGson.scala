package hnct.fudivery.lib.mongodb

import java.lang.reflect.Type
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import com.google.gson.GsonBuilder
import com.google.gson.JsonSerializer
import com.google.gson.JsonSerializationContext
import org.joda.time.DateTime
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonNull
import com.google.gson.JsonDeserializer
import com.google.gson.JsonDeserializationContext
import com.google.gson.reflect.TypeToken

object JsonProcGson {
  
  def toJson[T](obj: T) = gson.toJson(obj)
  
  def fromJson[T](json: String, clazz: Class[T]): T = gson.fromJson(json, clazz)
  
  def fromJson[T](json: String, t: Type): T = gson.fromJson(json, t)
  
  /* ------------------------------ HELPERS ----------------------------------------- */
  
  val DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC)
 
  private val gson = new GsonBuilder()
    .registerTypeHierarchyAdapter(classOf[Seq[Any]], new ListProc)
    .registerTypeHierarchyAdapter(classOf[Map[Any,Any]], new MapProc)
    .registerTypeHierarchyAdapter(classOf[Option[Any]], new OptionProc)
    .registerTypeAdapter(classOf[DateTime], new DateTimeProc)
    .excludeFieldsWithoutExposeAnnotation()
    .setPrettyPrinting()
    .create();
  
  private class ListProc extends JsonSerializer[Seq[Any]] with JsonDeserializer[Seq[Any]] {
    override def serialize(src: Seq[Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      import scala.collection.JavaConverters._
      context.serialize(src.toList.asJava)
    }

    def deserialize(json: JsonElement, typeOfJson: Type, context: JsonDeserializationContext): Seq[Any] = {
      val listType = new TypeToken[List[Any]](){}.getType();
      fromJson(json.getAsString, listType)
    }
  }
 
  private class MapProc extends JsonSerializer[Map[Any,Any]] {
    override def serialize(src: Map[Any,Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      import scala.collection.JavaConverters._
      context.serialize(src.asJava)
    }
  }
 
  private class OptionProc extends JsonSerializer[Option[Any]] {
    override def serialize(src: Option[Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      src match {
        case None => JsonNull.INSTANCE
        case Some(v) => context.serialize(v)
      }
    }
  }
 
  private class DateTimeProc extends JsonSerializer[DateTime] {
    override def serialize(src: DateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      new JsonPrimitive(DATE_TIME_FORMATTER.print(src))
    }
  }

}