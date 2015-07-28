package hnct.fudivery.lib.mongodb

import hnct.fudivery.mongodb.model._
import scala.io.Source
import scala.util.Random
import hnct.lib.utility.Logable
import hnct.fudivery.mongodb.MongoDb
import hnct.lib.config.Configuration
import hnct.lib.config.ConfigurationFormat
import java.io.File
import hnct.lib.utility.StringUtil


case class MockDataConfig(
    dataFile : String,
    imgFolder: String
)

/**
 * @author tduccuong
 */
object MockDataGenerator extends App with Logable {
  val db = new MongoDb("localhost", 27017, "fudivery")
  
  // empty database before creating mock data
  db.emptyDb
  log.debug("db has been emptied!")

  var restaurants = Vector[RestaurantM]()
  var feedbacks = Vector[FeedbackM]()
  var users = Vector[UserM]()
  var discounts = Vector[DiscountM]()
  var foodItems = Vector[FoodItemM]()
  var foodDimensions = Vector[FoodDimensionM]()
  var foodKeywords = Vector[FoodGroupM]()
  var tokens = Vector[FoodKeywordM]()
  
  val cols = Set[String](
    "RestaurantM",
    "FeedbackM",
    "UserM",
    "DiscountM",
    "FoodItemM",
    "FoodDimensionM",
    "FoodGroupM"
  )
  
  val random = Random
  var col: String = null
  
  
  val config = Configuration.read("conf/test/mockdataconfig.json", classOf[MockDataConfig], ConfigurationFormat.JSON)
  
  val fileName = config.getOrElse(throw new RuntimeException("Can't find configuration file")).dataFile
  val imgFolder = config.getOrElse(throw new RuntimeException("Can't find configuration file")).imgFolder
  
	val imgs = new File(imgFolder).listFiles.filter(_.getName.endsWith(".jpg")).map(_.getName)
  
  def imgArrayGen(nElem: Int) = {
    var strArr = Vector[String]()
    for (i <- 1 to random.nextInt(nElem))
      strArr = strArr :+ imgs(random.nextInt(imgs.size))
    strArr
  }
  
  for (line <- Source.fromFile(fileName).getLines()) {
  	log.debug("current line: "+line)
    if (!line.startsWith("#") && !line.isEmpty()) {
      if (cols.contains(line)) col = line
      else {
        val param = line.split(",").map(_.trim())
        col match {
          case "FoodDimensionM" => 
            foodDimensions = foodDimensions :+ FoodDimensionM(param(0), "", 0, 0, 0)
            
          case "FoodGroupM" => 
            val fd = foodDimensions.filter(_.name == param(0))(0)
            val fkw = FoodGroupM(param(1), fd._id, 0, 0)
            foodKeywords = foodKeywords :+ fkw
            // create keyword tokens
            StringUtil.wordCombiOf(param(1)) foreach { token =>
            	tokens = tokens :+ FoodKeywordM(token, token.split(" ").size, fkw._id)
            }
          
          case "DiscountM" => 
            discounts = discounts :+ DiscountM(param(0), "", imgArrayGen(2), param(1).toDouble)
          
          case "UserM" => 
            users = users :+ UserM(param(0), imgArrayGen(3), "", param(1), param(1), Seq(UserRole.USER))
          
          case "RestaurantM" =>
            restaurants = restaurants :+ RestaurantM(param(0), "", imgArrayGen(3), "", param(1).toDouble, param(2).toDouble, "", Seq())
          
          case "FoodItemM" => 
            val keywords = param(1).split(";").map(_.trim())
            var kwIds = Vector[String]()
            for (keyword <- keywords) {
              println("Working with keyword "+keyword)
              foodKeywords.filter(_.name == keyword).foreach { fkw => kwIds = kwIds :+ fkw._id }
            }
            val resInd = random.nextInt(restaurants.size)
            foodItems = foodItems :+ FoodItemM(
                param(0), 
                "",
                imgArrayGen(6), 
                kwIds,
                Seq(), 
                RestaurantE(restaurants(resInd)._id, restaurants(resInd).name, Pair(restaurants(resInd).lat, restaurants(resInd).lon), ""),
                Seq()
            )
        }
      }
    }
  }
  
  def saveImgs(objId: String, imgs: Seq[String]) = {
    imgs foreach { img => db.saveFile(imgFolder+"/"+img, objId+ModelBuilder.IMG_FILENAME_SEPERATOR+img) }
  }
  
  // persist in db
  foodDimensions foreach { obj => 
    db.useCol[FoodDimensionM].insert(obj.toDbObject)
  }
  
  foodKeywords foreach { obj => 
    db.useCol[FoodGroupM].insert(obj.toDbObject)
  }
  
  tokens foreach { obj => 
    db.useCol[FoodKeywordM].insert(obj.toDbObject)
  }
  
  foodItems foreach { obj => 
    db.useCol[FoodItemM].insert(obj.toDbObject)
    saveImgs(obj._id, obj.photos)
  }
  
  restaurants foreach { obj => 
    db.useCol[RestaurantM].insert(obj.toDbObject)
    saveImgs(obj._id, obj.photos)
  }
  
  discounts foreach { obj => 
    db.useCol[DiscountM].insert(obj.toDbObject)
    saveImgs(obj._id, obj.photos)
  }
  
  users foreach { obj => 
    db.useCol[UserM].insert(obj.toDbObject)
    saveImgs(obj._id, obj.photos)
  }
  
  println("completed!")
}