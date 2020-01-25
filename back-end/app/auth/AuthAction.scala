package auth

import api.Defs
import javax.inject.Inject
import play.api.mvc._
import shared.models.ClientUser
import wvlet.log.{LogLevel, LogSupport, Logger}
import scala.concurrent.{ExecutionContext, Future}

case class UserRequest[A](user: ClientUser, sessionId: String, request: Request[A]) extends WrappedRequest[A](request)

class AuthAction @Inject()(bodyParser: BodyParsers.Default, authService: AuthService)(implicit ec: ExecutionContext) extends ActionBuilder[UserRequest, AnyContent] with LogSupport {
  Logger.setDefaultLogLevel(LogLevel.INFO)

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec
  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {
    extractSession(request) map {
      sessionId => {
        authService.findUserSession(sessionId).map({
          user =>
            user match {
              case Some(user) => block(UserRequest(user, sessionId, request)) // sessionId was valid - proceed!
              case None => Future.successful(Results.Redirect("/login")) // sessionId was invalid - go to loginPage
            }
        }).flatten
      }
    } getOrElse Future.successful(Results.Unauthorized)     // no sessionId was sent - return 401
  }

  private def extractSession[A](request: Request[A]): Option[String] = {
    info(request.cookies.get(Defs.PLAY_SESSION))
    request.cookies.get("PLAY_SESSION") collect {
      case session: Cookie => {
        session.value
      }
    }
  }

}