package application

import org.scalajs.dom
import org.scalajs.dom.Event
import japgolly.scalajs.react.extra.router.{BaseUrl, Router}
import router.RouterConfiguration

object LoginPage {

  def main(args: Array[String]): Unit = {
    dom.window.addEventListener("load", initApp)
  }

  val router = Router(BaseUrl.fromWindowOrigin / "", RouterConfiguration.config)

  def initApp(e: Event) = router().renderIntoDOM(dom.document.getElementById("App"))

}

