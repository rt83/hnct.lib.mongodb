package hnct.fudivery.lib.mongodb

import hnct.fudivery.mongodb.model._
import scala.io.Source
import scala.util.Random
import hnct.lib.utility.Logable
import hnct.fudivery.mongodb.MongoDb


/**
 * @author tduccuong
 */
object MockDataGenerator extends App with Logable {
  val db = new MongoDb("localhost", 27017, "fudivery")
  
  // empty database before creating mock data
  db.emptyDb
  log.debug("db has been emptied!")

  var ingredients = Vector[IngredientM]()
  var foodTypes = Vector[FoodTypeM]()
  var foodCats = Vector[FoodCategoryM]()
  var restaurants = Vector[RestaurantM]()
  var feedbacks = Vector[FeedbackM]()
  var users = Vector[UserM]()
  var roles = Vector[UserRoleM]()
  var rankDims = Vector[RankDimM]()
  var discounts = Vector[DiscountM]()
  var items = Vector[FoodItemM]()
  
  val cols = Set[String](
    "IngredientM",
    "FoodTypeM",
    "FoodCategoryM",
    "RestaurantM",
    "FeedbackM",
    "UserM",
    "RankDimM",
    "DiscountM",
    "FoodItemM",
    "UserRoleM"
  )
  
  val random = Random
  var col: String = null
  for (line <- Source.fromFile("/home/tduccuong/Projects/hnct/hnct.fudivery.mongodb/src/main/resources/fudivery-mock.dat").getLines()) {
//  for (line <- Source.fromFile("/Users/tduccuong/Projects/hnct/hnct.fudivery.mongodb/src/main/resources/fudivery-mock.dat").getLines()) {
  	log.debug("current line: "+line)
    if (!line.startsWith("#") && !line.isEmpty()) {
      if (cols.contains(line)) col = line
      else {
        val param = line.split(",")
        col match {
          case "IngredientM" => 
            ingredients = ingredients :+ IngredientM(param(0), param(1))
          case "FoodTypeM" => 
            foodTypes = foodTypes :+ FoodTypeM(param(0), param(1))
          case "FoodCategoryM" => 
            foodCats = foodCats :+ FoodCategoryM(param(0), param(1))
          case "UserRoleM" => 
            roles = roles :+ UserRoleM(param(0), param(1))
          case "RankDimM" => 
            rankDims = rankDims :+ RankDimM(param(0), param(1))
          case "DiscountM" => 
            discounts = discounts :+ DiscountM(param(0), param(1), param(2).toDouble)
          case "UserM" => 
            users = users :+ UserM(param(0), param(1), param(2), param(3), Seq(roles(random.nextInt(roles.size))._id), Seq())
          case "RestaurantM" =>
            restaurants = restaurants :+ RestaurantM(param(0), "", param(1), param(2).toDouble, param(3).toDouble, "", Seq())
          case "FoodItemM" => 
            val maxIngrd = 10
            var ingrds = Vector[Pair[String, String]]()
            for (i <- 1 to random.nextInt(maxIngrd)+1) {
              val index = random.nextInt(ingredients.size)
              ingrds = ingrds :+ Pair(ingredients(index).name, ingredients(index)._id)
            }
            
            val maxPhoto = 6
            var photos = Vector[String]()
            for (i <- 1 to random.nextInt(maxPhoto))
              photos = photos :+ "photo" + i
              
            val maxFoodType = 3
            var fts = Vector[Pair[String, String]]()
            for (i <- 1 to random.nextInt(maxFoodType)+1) {
              val index = random.nextInt(foodTypes.size)
              fts = fts :+ Pair(foodTypes(index).name, foodTypes(index)._id)
            }
            
            val maxFoodCat = 3
            var fcs = Vector[Pair[String, String]]()
            for (i <- 1 to random.nextInt(maxFoodCat)+1) {
              val index = random.nextInt(foodCats.size)
              fcs = fcs :+ Pair(foodCats(index).name, foodCats(index)._id)
            }
            
            val maxDiscount = 2
            var dcs = Vector[Pair[String, String]]()
            for (i <- 1 to random.nextInt(maxDiscount)+1) {
              val index = random.nextInt(discounts.size)
              dcs = dcs :+ Pair(discounts(index).name, discounts(index)._id)
            }
            
            val resInd = random.nextInt(restaurants.size)
            items = items :+ FoodItemM(
                param(0), 
                ingrds, 
                photos, 
                "", 
                Seq(), 
                fts,
                fcs,
                (restaurants(resInd).name, restaurants(resInd)._id, restaurants(resInd).lat, restaurants(resInd).lon, restaurants(resInd).addr),
                dcs
            )
        }
      }
    }
  }
  
  // persist in db
  items foreach { obj => db.useCol[FoodItemM].insert(obj.toDbObject) }
  ingredients foreach { obj => db.useCol[IngredientM].insert(obj.toDbObject) }
  foodTypes foreach { obj => db.useCol[FoodTypeM].insert(obj.toDbObject) }
  foodCats foreach { obj => db.useCol[FoodCategoryM].insert(obj.toDbObject) }
  roles foreach { obj => db.useCol[UserRoleM].insert(obj.toDbObject) }
  users foreach { obj => db.useCol[UserM].insert(obj.toDbObject) }
  rankDims foreach { obj => db.useCol[RankDimM].insert(obj.toDbObject) }
  restaurants foreach { obj => db.useCol[RestaurantM].insert(obj.toDbObject) }
  discounts foreach { obj => db.useCol[DiscountM].insert(obj.toDbObject) }
  
  println("completed!")
}