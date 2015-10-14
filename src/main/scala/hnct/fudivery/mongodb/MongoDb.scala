package hnct.fudivery.mongodb

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.gridfs.GridFS
import java.io.File
import java.io.FileInputStream
import com.mongodb.casbah.gridfs.GridFSDBFile
import scala.reflect.runtime.universe._
import scala.reflect.ClassTag

class MongoDb(host: String, port: Int, dbName: String) {
  private val mongoClient = MongoClient(host, port)
  
  private val db = mongoClient(dbName)
  private var cols = Map[String, MongoCollection]()
  
  /* GridFS for storing binaries (including images) */
  private val gridFs = GridFS(db);
  
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
  def useCol(name: String): MongoCollection = {
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
  def useCol[T](implicit tag: TypeTag[T]): MongoCollection = 
    useCol(typeTag[T].mirror.runtimeClass(typeTag[T].tpe).getSimpleName)
  
  /**
   * Query documents that match q.
   * Ensure that the will-be-loaded models's is always consistent with the current model version
   */
  def query(colName: String)(q: DBObject): MongoCursor = {
    val queryList = List(ModelBuilder.MODEL_QUERY, q)
    useCol(colName).find(MongoDBObject("$and" -> queryList))
  }
  
  /**
   * Query documents that match q.
   * Ensure that the will-be-loaded models's is always consistent with the current model version
   */
  def query[T](q: DBObject)(implicit tag: TypeTag[T]): MongoCursor = {
    val queryList = List(ModelBuilder.MODEL_QUERY, q)
    useCol(typeTag[T].mirror.runtimeClass(typeTag[T].tpe).getSimpleName).find(MongoDBObject("$and" -> queryList)) 
  }
  
  /**
   * Query all documents in a collection.
   * Ensure that the will-be-loaded models's is always consistent with the current model version
   */
  def query[T](implicit tag: TypeTag[T]): MongoCursor = {
    useCol(typeTag[T].mirror.runtimeClass(typeTag[T].tpe).getSimpleName).find(ModelBuilder.MODEL_QUERY) 
  }
  
  /**
   * Query all documents in a collection.
   * Ensure that the will-be-loaded models's is always consistent with the current model version.
   */
  def query[T](colName: String): MongoCursor = {
    useCol(colName).find(ModelBuilder.MODEL_QUERY) 
  }
  
  /**
   * Save binary file to db.
   */
  def saveFile(fileName: String, fileNameInDb: String): Unit = {
    val fileInputStream =new FileInputStream(new File(fileName))
    val gfsFile = gridFs.createFile(fileInputStream)
    gfsFile.filename = fileNameInDb
    gfsFile.save()
  }
  
  def getFile(fileNameInDb: String): Option[GridFSDBFile] = {
    gridFs.findOne(fileNameInDb)
  }
  
  def deleteFile(fileNameInDb: String): Unit = {
    gridFs.remove(fileNameInDb)
  } 
  
}
