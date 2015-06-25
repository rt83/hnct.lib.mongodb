package hnct.fudivery.lib.mongodb

import hnct.fudivery.mongodb.model._
import hnct.fudivery.mongodb.MongoDb
import scala.io.Source


/**
 * @author tduccuong
 */
object MockDataGenerator extends App {
  val db = new MongoDb("localhost", 27017, "fudivery")

  var ingredients = Vector[IngredientM]()
  var foodTypes = Vector[FoodTypeM]()
  var foodCats = Vector[FoodCategoryM]()
  var restaurants = Vector[RestaurantM]()
  var feedbacks = Vector[FeedbackM]()
  var users = Vector[UserM]()
  var rankDims = Vector[RankDimM]()
  var discounts = Vector[DiscountM]()
  var items = Vector[ItemM]()
  
  val cols = Set[String](
    "IngredientM",
    "FoodTypeM",
    "FoodCategoryM",
    "RestaurantM",
    "FeedbackM",
    "UserM",
    "RankDimM",
    "DiscountM",
    "ItemM"
  )
  
  var col: String = null
  for (line <- Source.fromFile("fudivery-mock.dat").getLines()) {
    if (cols.contains(line)) col = line
    else {
      if (!line.startsWith("#") && !line.isEmpty()) col match {
        case "IngredientM" => ingredients = ingredients :+ IngredientM(line, line)
        case "FoodTypeM" => foodTypes = foodTypes :+ FoodTypeM(line, line)
        case "FoodCategoryM" => foodCats = foodCats :+ FoodCategoryM(line, line)
        case "RestaurantM" => {
          val split = line.split(",")
          restaurants = restaurants :+ RestaurantM(split(0), "", split(1), split(2).toDouble, split(3).toDouble, "", Seq())
        }
        case "FeedbackM" => feedbacks = feedbacks :+ FeedbackM(line, line)
      }
    }
  }  
}