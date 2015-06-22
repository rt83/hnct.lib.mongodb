package hnct.fudivery.lib.mongodb

import hnct.fudivery.mongodb.model._
import hnct.fudivery.mongodb.MongoDb


/**
 * @author tduccuong
 */
object MockDataGenerator extends App {
  val db = new MongoDb("localhost", 27017, "fudivery")

  val ingredients = List(
    IngredientM("Noodle", ""),
    IngredientM("Rice", ""),
    IngredientM("Pepper", ""),
    IngredientM("Chily", ""),
    IngredientM("Chicken", ""),
    IngredientM("Beef", "")
  )
  
  val foodTypes = List(
    FoodTypeM("Starter", ""),
    FoodTypeM("Main course", ""),
    FoodTypeM("Breakfast", ""),
    FoodTypeM("Lunch", "")
  )
  
  
  
  val discountPrograms = List(
//    DiscountProgramM()
  )
  
  val items = List()
}