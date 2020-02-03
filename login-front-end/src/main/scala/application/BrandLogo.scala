package application

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra._

object BrandLogo {

  case class Props()

  val component = ScalaComponent.builder[Props]("BrandLogo")
    .stateless
    .render_P(p => <.div(
        ^.className:="section",
        <.div(
          ^.className:="card-image",
          <.img(^.src:="assets/images/roundText.png", ^.paddingLeft:="5vh", ^.paddingRight:="5vh"),
        )
      ),
    )
    .build

    def apply() = component(Props())
}
