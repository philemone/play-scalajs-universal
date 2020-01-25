package application

import facades.materialize.Sidenav
import japgolly.scalajs.react.extra.router.{BaseUrl, Router}
import org.scalajs.dom
import org.scalajs.dom.Event
import router.RouterConfiguration
import io.udash.wrappers.jquery._

import scala.scalajs.js

object Application {

  def main(args: Array[String]): Unit = {
    println("init application")
    dom.window.addEventListener("load", initApp)
    dom.window.addEventListener("load", initSideNav)
  }

  val router = Router(BaseUrl.fromWindowOrigin / "", RouterConfiguration.config)

  def initSideNav(e: Event) = {
    jQ( () => {Sidenav.init(dom.document.querySelectorAll(".sidenav"), js.Dynamic.literal()); null} )
  }

  def initApp(e: Event) = router().renderIntoDOM(dom.document.getElementById("App"))

}
