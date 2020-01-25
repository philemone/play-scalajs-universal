package application.menucomp

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl
import router.Pages

object AmazingPageComp {

  case class Props(router: RouterCtl[Pages])
  case class State()

  final class Backend($: BackendScope[Props, State]) {

    def render(p: Props, s: State): VdomElement = {
      <.div(
        "Amazing Page !"
      )
    }
  }

  val component = ScalaComponent.builder[Props]("AmazingPageComp")
    .initialState( State( ) )
    .renderBackend[Backend]
    .build

    def apply(router: RouterCtl[Pages]) = component(Props(router))
}