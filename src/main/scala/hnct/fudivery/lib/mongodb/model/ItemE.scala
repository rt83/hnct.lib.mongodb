package hnct.fudivery.lib.mongodb.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author tduccuong
 */
class ItemE extends EntityE {
  @Expose 
  @SerializedName("name")
  private var _name: String = null
  def name_=(value: String) = _name = value
  def name = _name
  
  @Expose 
  @SerializedName("ingredients")
  private var _ingredients = Map[String, String]("a" -> "1", "b" -> "2", "c" -> "3")
  def ingredients_=(values: Map[String, String]) = _ingredients = values
  def ingredients = _ingredients
}