package mongodb

import cn.playscala.mongo.{MongoClient, MongoDatabase}
import play.api.Configuration

class MongoConnection(configuration: Configuration, dbName: String = "appDatabase") {

  def mongoClient: MongoClient = MongoClient(s"${configuration.get[String]("mongodb.uri")}")

  def database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(CodecRegistry.codecRegistry)

}
