package facades.zxcvbn

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobalScope

trait Feedback extends js.Object {
  var suggestions: js.Array[String]
  var warning: String
}

trait PasswordGuess extends js.Object {
  var score: Int
  var guesses: Double
  var guesses_log10: Double
  var password: String
  var feedback: Feedback
}

@JSGlobalScope
object ZXCVBN extends js.Object {
  def zxcvbn(n: String): PasswordGuess = js.native
}
