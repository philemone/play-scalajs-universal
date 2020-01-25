package components

import java.util.UUID.randomUUID

import facades.materialize.Modal
import io.udash.wrappers.jquery.jQ
import japgolly.scalajs.react.vdom.html_<^.{<, VdomElement, ^}
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object LoadingModal {

  case class State()

  private val modalId = randomUUID.toString

  final class Backend($: BackendScope[Unit, State]) {
    def render(s: State): VdomElement = {
      <.div(^.className:="valign-wrapper",
        <.div(^.id:=modalId , ^.className:="modal transparent z-depth-0 ", ^.top:="40%!important",
          <.div(^.className:="modal-content center",
            <.div(^.className:="preloader-wrapper active",
              <.div(^.className:="spinner-layer spinner-yellow-only",
                <.div(^.className:="circle-clipper left", <.div(^.className:="circle") ),
                <.div(^.className:="gap-patch", <.div(^.className:="circle") ),
                <.div(^.className:="circle-clipper right", <.div(^.className:="circle") )
              )
            ),
          ),
        )
      )
    }
  }

  def closeModal() = jQ( () => {
    Modal.getInstance(dom.document.getElementById(modalId)).close()
    null
  })

  def openModal() = jQ( () => {
    Modal.getInstance(dom.document.getElementById(modalId)).open()
    null
  })

  def openModalCB() = Callback(openModal())
  def closeModalCB() = Callback(closeModal())

  val component = ScalaComponent.builder[Unit]("LoadingModal")
    .initialState( State( ) )
    .renderBackend[Backend]
    .componentDidMount($ => Callback {
      jQ( () => {
        Modal.init(dom.document.getElementById(modalId), js.Dynamic.literal("endingTop" -> "40%", "dismissible" -> false));
        null} )
    })
    .build

    def apply() = component()
}
