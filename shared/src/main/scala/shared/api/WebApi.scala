package shared.api

import shared.models.{ClientUser, LoginUserError, User}

import scala.concurrent.Future

trait WebApi {

  def getLoggedUser(): Future[Option[ClientUser]]
  def signIn(email: String, password: String): Future[Either[LoginUserError, ClientUser]]
  def signUp(password: String, email: String): Future[Either[LoginUserError, User]]
  def signOut(): Future[Unit]

}