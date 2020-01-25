package scala.controllers

import java.nio.ByteBuffer
import api.WebApiImpl
import javax.inject._
import auth.{AuthAction, UnAuthAction}
import boopickle.Pickler
import shared.api.WebApi
import play.api.mvc._
import boopickle.Default._
import play.api.Configuration
import wvlet.log.{LogLevel, LogSupport, Logger}
import scala.concurrent.ExecutionContext.Implicits.global

case class AutowireContext(playRequest: RequestHeader)

trait AutowireServerWithContext[T] extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  override def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)
  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)
  def routes(target: T): Router
  def createImpl(autowireContext: AutowireContext, configuration: Configuration): T
}

object ServerUserApi extends AutowireServerWithContext[WebApi] {
  override def routes(target: WebApi) = route[WebApi](target)
  override def createImpl(autowireContext: AutowireContext, configuration: Configuration): WebApi = new WebApiImpl(autowireContext, configuration)
}

@Singleton
class Application @Inject()(cc: ControllerComponents, authAction: AuthAction, unAuthAction: UnAuthAction, configuration: Configuration) extends AbstractController(cc) with LogSupport {
  Logger.setDefaultLogLevel(LogLevel.INFO)

  def index = authAction { implicit req => Ok(views.html.index("UniversalApp")) }

  def login = unAuthAction { implicit req => Ok(views.html.login("Login Page")) }

  def apiContext[T](server: AutowireServerWithContext[T])(path: String) = {
    Action.async(parse.raw) {
      implicit request =>
        val b = request.body.asBytes(parse.UNLIMITED).get
        val req = autowire.Core.Request(path.split("/"), Unpickle[Map[String, ByteBuffer]].fromBytes(b.asByteBuffer))
        val requestSession = server.createImpl(
          AutowireContext(playRequest = request),
          configuration
        )
        server.routes(requestSession)(req).map(buffer => {
          val data = Array.ofDim[Byte](buffer.remaining())
          buffer.get(data)
          Ok(data)
        })
    }
  }

  def api = apiContext(ServerUserApi)_

}
