package hnct.fudivery.mongodb

import com.mongodb.casbah.Imports._
import hnct.fudivery.mongodb.model.ModelBuilder
import com.mongodb.casbah.gridfs.GridFS
import java.io.File
import java.io.FileInputStream
import com.mongodb.casbah.gridfs.GridFSDBFile

class MongoDb(host: String, port: Int, dbName: String) {
  private val mongoClient = MongoClient(host, port)
  
  private val db = mongoClient(dbName)
  private var cols = Map[String, MongoCollection]()
  
  /* GridFS for storing binaries (including images) */
  val gridFs = GridFS(db);
  
  /**
   * Close the current connection
   */
  def closeDb = mongoClient.close()
  
  /**
   * Empty the database
   */
  def emptyDb = db.dropDatabase()
  
  /**
   * Return a MongoDBCollection collection given its name
   */
  def useCol(name: String) = {
    if (cols.contains(name)) cols(name)
    else {
      val col = db(name)
      cols = cols + (name -> col)
      col
    }
  }
  
  /**
   * Return a MongoDBCollection given its Scala class name
   */
  def useCol[T](implicit tag: reflect.ClassTag[T]): MongoCollection = useCol(tag.runtimeClass.getSimpleName)
  
  /**
   * Ensure that the will-be-loaded models's is always consistent with the current model version
   */
  def query(colName: String)(q: DBObject) = {
    val queryList = List(ModelBuilder.MODEL_QUERY, q)
    useCol(colName).find(MongoDBObject("$and" -> queryList))
  }
  
  /**
   * Ensure that the will-be-loaded models's is always consistent with the current model version
   */
  def query[T](q: DBObject)(implicit tag: reflect.ClassTag[T]) = {
    val queryList = List(ModelBuilder.MODEL_QUERY, q)
    useCol(tag.runtimeClass.getSimpleName).find(MongoDBObject("$and" -> queryList)) 
  }
  
  /**
   * Query all documents in a collection
   */
  def query[T](implicit tag: reflect.ClassTag[T]) = {
    useCol(tag.runtimeClass.getSimpleName).find() 
  }
  
  /**
   * Save binary file to db.
   */
  def saveFile(fileName: String, fileNameInDb: String) = {
    val fileInputStream =new FileInputStream(new File(fileName))
    val gfsFile = gridFs.createFile(fileInputStream)
    gfsFile.filename = fileNameInDb
    gfsFile.save()
  }
  
  def getFile(fileNameInDb: String): Option[GridFSDBFile] = {
    gridFs.findOne(fileNameInDb)
  }
  
  def deleteFile(fileNameInDb: String) = {
    gridFs.remove(fileNameInDb)
  } 
  
}
