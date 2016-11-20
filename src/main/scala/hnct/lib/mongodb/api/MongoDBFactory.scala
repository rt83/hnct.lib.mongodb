package hnct.lib.mongodb.api

import com.google.inject.assistedinject.Assisted

/**
	* Created by Ryan on 11/20/2016.
	*/
trait MongoDBFactory {
	def create(@Assisted("host") host : String, port : Int, @Assisted("db") db : String) : MongoDb
}
