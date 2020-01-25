package auth

import cn.playscala.mongo.MongoCollection
import javax.inject.Inject
import mongodb.MongoConnection
import play.api.Configuration
import shared.models.{ClientUser, User}
import play.api.libs.json.Json._
import wvlet.log.{LogLevel, LogSupport, Logger}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthService @Inject()(config: Configuration) extends LogSupport {
  Logger.setDefaultLogLevel(LogLevel.INFO)

  lazy val collection: MongoCollection[User] = new MongoConnection(config).database.getCollection("users")

  def findUserSession(sessionId: String): Future[Option[ClientUser]] = {
    info(s"findUserSession: $sessionId")
    for(
      list <- collection.find(obj("sessionId" -> sessionId)).list()
    ) yield {
      if(list.length != 1) None
      else list.headOption.map(_.clientUser)
    }
  }

}