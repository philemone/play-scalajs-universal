package application

import japgolly.scalajs.react.vdom.Attr
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._

object Navbar {

    def render: VdomElement = {
        <.div(^.className:="nav-wrapper cyan accent-4",
          <.div(^.className:="row",
            <.div(^.className:="cols s12",
              <.a(^.href:="#", Attr("data-target"):="leftSidenav", ^.className:="left sidenav-trigger hide-on-medium-and-up", <.i(^.className:="material-icons", "menu")),
              <.a(^.href:="#", ^.className:="brand-logo center black-text", <.img(^.className:="logo-img left", ^.src:="/assets/images/logoTitle.png"), "")
            )
          )
        )
    }

  val component = ScalaComponent.builder[Unit]("Navbar Component")
    .render_(render)
    .build

  def apply() = component()
}
