package settings

import scala.scalajs.js

case class ToastGenerator(text: String, classes: String = "amber darken-1 black-text") {
  val set = js.Dynamic.literal("html" -> text, "classes" -> classes)
}

