package api

import cn.playscala.mongo.MongoCollection
import mongodb.{MongoConnection, MongoDatabases}
import org.bson.types.ObjectId
import org.mongodb.scala.MongoWriteException
import play.api.Configuration
import play.api.libs.json.Json.obj
import play.api.mvc.Cookie
import shared.api.WebApi
import shared.models.{ClientUser, IncorrectCredentials, LoginUserError, UnknownUserError, User, UserExist}
import wvlet.log.LogSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.controllers.AutowireContext

class WebApiImpl(context: AutowireContext, configuration: Configuration) extends WebApi with LogSupport {

  private final val cookieSession = context.playRequest.cookies.get(Defs.PLAY_SESSION).getOrElse(Cookie("noCookieSet", "noCookieSet")).value

  def getLoggedUser(): Future[Option[ClientUser]] = {
    lazy val collection: MongoCollection[User] = new MongoConnection(configuration).database.getCollection(MongoDatabases.USERS_DB)
    for(
        list <- collection.find(obj("sessionId" -> cookieSession)).list()
      ) yield {
        if(list.length != 1) None
        else list.headOption.map(_.clientUser)
      }
  }

  def signIn(email: String, password: String): Future[Either[LoginUserError, ClientUser]] = {
    lazy val emailLowerCase = email.toLowerCase()
    info(s"email $emailLowerCase $password")
    lazy val collection: MongoCollection[User] = new MongoConnection(configuration).database.getCollection(MongoDatabases.USERS_DB)
    for {
      list <- collection.find(obj("email" -> emailLowerCase, "password" -> password)).list()
      _ <- collection.updateOne(obj("email" -> emailLowerCase, "password" -> password), obj("$set" -> obj("sessionId" -> cookieSession))).map(
        _ => collection.updateMany(obj("sessionId" -> cookieSession, "email" -> obj("$not"-> obj("$eq" -> emailLowerCase))), obj("$set" -> obj("sessionId" -> "loggedOut")))
      )
    } yield {
      if(list.length > 1) Left(UnknownUserError("User in DB is ambiguous"))
      else if(list.isEmpty) Left(IncorrectCredentials())
      else Right(list.head.clientUser)
    }
  }

  def signUp(email: String, password: String): Future[Either[LoginUserError, User]] = {
    lazy val emailLowerCase = email.toLowerCase()
    info(s"signup $emailLowerCase")
    lazy val collection: MongoCollection[User] = new MongoConnection(configuration).database.getCollection(MongoDatabases.USERS_DB)
    val userToInsert = User(new ObjectId().toString, emailLowerCase, password, cookieSession)
      for {
        _ <- collection.insertOne(userToInsert) recoverWith {case t: MongoWriteException => Future.failed(UserExist(email))}
        _ <- collection.updateMany(obj("sessionId" -> cookieSession, "email" -> obj("$not" -> obj("$eq" -> emailLowerCase))), obj("$set" -> obj("sessionId" -> "loggedOut")))
        user <- collection.find(obj("email" -> emailLowerCase)).list()
      } yield {
        if (user.length < 1) Left(UnknownUserError("User was not added, please try again later"))
        else if (user.length == 1) Right(user.head)
        else Left(UnknownUserError("Some error occurred, please try again later"))
      }

    } recoverWith {
    case t: UserExist => Future.successful(Left(UserExist(email)))
    case t: Throwable => Future.successful(Left(UnknownUserError(t.getMessage)))
  }

  def signOut(): Future[Unit] = {
    info(s"signOut")
    lazy val collection: MongoCollection[User] = new MongoConnection(configuration).database.getCollection(MongoDatabases.USERS_DB)
    for{
      logoutSessions <- collection.updateMany(obj("sessionId" -> cookieSession), obj("$set" -> obj("sessionId" -> "loggedOut")))
    } yield {
      info(s"COUNT: ${logoutSessions.getMatchedCount}")
    }
  }


}
