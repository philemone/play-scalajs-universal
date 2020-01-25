package application.useraccountsetting

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object UserAccountSettingPanel {

  case class Props()

  case class State()

  final class Backend($: BackendScope[Props, State]) {
    def render(p: Props, s: State): VdomElement = {
      <.div("User Account Setting Panel")
    }
  }

  val component = ScalaComponent.builder[Props]("UserAccountSettingPanel")
    .initialState( State( ) )
    .renderBackend[Backend]
    .build
    
    def apply() = component(Props())
}