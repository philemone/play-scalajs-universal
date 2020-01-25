package components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra._

object PasswordField {

  case class Props(password: String, onChangeCallback: (ReactEventFromInput) => Callback, label: String, id: String)

  case class State()

  final class Backend($: BackendScope[Props, State]) {
    def render(p: Props, s: State): VdomElement = {
      <.div(
        ^.className:="input-field",
        <.input(
          ^.id:=p.id,
          ^.`type`:="password",
          ^.className:="validate",
          ^.value:=p.password,
          ^.required:=true,
          ^.onChange ==> ((e: ReactEventFromInput) => p.onChangeCallback(e))
        ),
        <.label(^.`for`:=p.id, ^.className:="active", p.label)
      )
    }
  }

  val component = ScalaComponent.builder[Props]("PasswordField")
    .initialState( State( ) )
    .renderBackend[Backend]
    .build

    def apply(password: String, onChangeCallback: (ReactEventFromInput) => Callback, label: String = "Password", id: String = "password") =
      component(Props(password, onChangeCallback, label, id))
}
