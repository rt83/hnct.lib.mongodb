package hnct.fudivery.lib.mongodb.model

import org.json4s.jackson.Serialization
import org.json4s.NoTypeHints
import com.mongodb.util.JSON
import com.mongodb.DBObject

/**
 * @author tduccuong
 */

trait Serializable {
  private val formats = Serialization.formats(NoTypeHints)
  
  def toJson = Serialization.write(this)(formats)
  
  def toDbObject = JSON.parse(toJson).asInstanceOf[DBObject]
}

object ModelBuilder {
  implicit val formats = Serialization.formats(NoTypeHints)
  
  def fromJson[T](json: String)(implicit m: Manifest[T]) = Serialization.read[T](json)
  
  def fromDbObject[T](dbo: DBObject)(implicit m: Manifest[T]) = fromJson[T](dbo.toString())
}

case class ItemE(
  _id: String, 
  name: String, 
  ingrds: Seq[String],
  photos: Seq[String],
  desc: String,
  feedbacks: Seq[Tuple3[String, String, Double]]
) extends Serializable