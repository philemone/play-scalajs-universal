package auth

import api.Defs
import javax.inject.Inject
import play.api.mvc._
import wvlet.log.{LogLevel, LogSupport, Logger}
import scala.concurrent.{ExecutionContext, Future}

class UnAuthAction @Inject()(bodyParser: BodyParsers.Default, authService: AuthService)(implicit ec: ExecutionContext) extends ActionBuilder[Request, AnyContent] with LogSupport {
  Logger.setDefaultLogLevel(LogLevel.INFO)

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    extractSession(request) map {
      sessionId => {
        authService.findUserSession(sessionId).map({
          case Some(_) => Future.successful(Results.Redirect("/"))
          case None => block(request)
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