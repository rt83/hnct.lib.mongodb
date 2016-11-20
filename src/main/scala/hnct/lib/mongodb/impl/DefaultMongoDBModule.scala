package hnct.lib.mongodb.impl

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.multibindings.Multibinder
import hnct.lib.mongodb.api.{MongoDBFactory, MongoDb}
import org.bson.codecs.configuration.{CodecRegistries, CodecRegistry}

/**
	* Created by Ryan on 11/20/2016.
	*/
class DefaultMongoDBModule extends AbstractModule {
	
	override def configure(): Unit = {
		val s  = Multibinder.newSetBinder(binder(), classOf[CodecRegistry])
		
		s.addBinding().toInstance(Registries.primitives)
		s.addBinding().toInstance(CodecRegistries.fromProviders(GeoJsonCodecsProvider))
		
		install(new FactoryModuleBuilder().
			implement(classOf[MongoDb], classOf[DefaultMongoDb]).
			build(classOf[MongoDBFactory]))
	}
	
}
