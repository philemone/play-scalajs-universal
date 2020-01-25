package mongodb

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import shared.models.User

object CodecRegistry {

  val codecRegistry = fromRegistries(fromProviders(
    classOf[User],
  ), DEFAULT_CODEC_REGISTRY )

}
