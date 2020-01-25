package facades.materialize

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

/**
 *
 * Materialize Modal simple facade
 *
 */

trait ModalElement extends js.Object {
  def open() = js.native
  def close() = js.native
}

@js.native
@JSGlobal("M.Modal")
object Modal extends js.Object{

  def init(elements: js.Any, options: js.Any): ModalElement = js.native

  def getInstance(element: js.Any): ModalElement = js.native

}