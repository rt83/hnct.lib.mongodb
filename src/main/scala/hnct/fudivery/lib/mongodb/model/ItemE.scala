package hnct.fudivery.lib.mongodb.model


import play.api.libs.json._
import org.json4s.jackson.Serialization
import org.json4s._
import scala.util.Try

/**
 * @author tduccuong
 */
class ItemE extends EntityE {
  private var _name: String = null
  def name_=(value: String) = _name = value
  def name = _name
  
  private var _ingredients = Vector[String]("a", "b", "c")
  def ingredients_=(values: Vector[String]) = _ingredients = values
  def ingredients = _ingredients
  
//  override def toJson() = {
//    Serialization.write(this)(formats)
//  }
  
//  override def toJson() = {
//    Json.prettyPrint(Json.toJson(this)(serializer))
//  }
  
  /* -------------------- Serializer and Deserializer ---------------- */
  
  
//  private val serializer = new Writes[ItemE] {
//    def writes(item: ItemE) = _toJson() ++ Json.obj(
//      "name" -> _name,
//      "ingredients" -> _ingredients
//    )
//  }
}

object ItemE {
  val formats = DefaultFormats + FieldSerializer[ItemE]()
  
  def toJson(obj: AnyRef): String = Serialization.write(obj)(formats)
  
  def fromJsonOption[T](jsonString: String)(implicit mf: Manifest[T]): Option[T] = Try(Serialization.read(jsonString)(formats, mf)).toOption
  
  def fromJson[T](json: String)(implicit mf: Manifest[T]): T = Serialization.read(json)(formats, mf)
}