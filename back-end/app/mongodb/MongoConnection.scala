package mongodb

import cn.playscala.mongo.{MongoClient, MongoDatabase}
import play.api.Configuration

class MongoConnection(configuration: Configuration, dbName: String = "appDatabase") {

  //NOTE: same mongodb URI is defined in application.conf - DRY - I know :)
  //def mongoClient: MongoClient = MongoClient(s"mongodb://scalauser:scalauserpass@mongodb:27017/$dbName")
  def mongoClient: MongoClient = MongoClient(s"${configuration.get[String]("mongodb.uri")}")

  def database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(CodecRegistry.codecRegistry)

}
