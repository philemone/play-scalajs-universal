package application

import apiservice.AjaxApiClient
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.api.WebApi
import autowire._
import scala.concurrent.ExecutionContext.Implicits.global
import boopickle.Default._
import components.{LoadingModal, PasswordField}
import facades.MD5
import facades.zxcvbn.ZXCVBN
import japgolly.scalajs.react.extra.router.RouterCtl
import router.{Pages, RedirectMain, SignInPage}
import shared.models.{LoginUserError, User}

object SignUpForm {

  case class Props(router: RouterCtl[Pages])

  case class State(password: String = "", cPassword: String = "", email: String = "", errorMsg: Option[String] = None)

  final class Backend($: BackendScope[Props, State]) {

    def onPasswordChange(s: State)= (e: ReactEventFromInput) => {
      $.setState(s.copy(password = e.target.value))
    }

    def onCPasswordChange(e: ReactEventFromInput, s: State) = {
      $.setState(s.copy(cPassword = e.target.value))
    }

    def onEmailChange(e: ReactEventFromInput, s: State) = {
      $.setState(s.copy(email = e.target.value))
    }

    def md5Hash(s: String): String = MD5.md5(s)

    def verifyPassword(p: Props, s: State)(e: ReactEventFromInput): CallbackTo[Unit] = {
      val passScore = ZXCVBN.zxcvbn(s.password).score + 1
        Callback(loadingModal.openModal()) >>
        e.preventDefaultCB >>
        $.modState(st => st.copy(errorMsg = None)) >>
        (if(!s.cPassword.equals(s.password)) $.modState(st => st.copy(cPassword = "", errorMsg = Some("Your passwords do not match")))  >> Callback(loadingModal.closeModal())
        else if(passScore < 2) $.modState(st => st.copy(errorMsg = Some(s"Your password is too weak ($passScore/5)"))) >> Callback(loadingModal.closeModal())
        else signUp(p, s)(e))
    }

    def signUp(p: Props, s: State)(e: ReactEventFromInput) = {
      def apiCall = AjaxApiClient[WebApi].signUp(s.email, md5Hash(s.password)).call().map {
        case Right(_: User) => p.router.set(RedirectMain).runNow()
        case Left(error: LoginUserError) => {
          $.modState(st => st.copy(password = "", cPassword = "", errorMsg = Some(error.getMessage))).runNow()
          loadingModal.closeModal()
        }
      }
      Callback(apiCall)
    }

    def classCPassword(s: State) = {
      if(s.password.isEmpty) ""
      else if(s.cPassword.equals(s.password)) "valid"
      else "invalid"
    }

    lazy val loadingModal = LoadingModal

    case class PasswordStrengthBar(msg: String, color: String, progress: String)

    def passwordStrength(s: State): PasswordStrengthBar = {
      val passScore = ZXCVBN.zxcvbn(s.password).score
      if(s.password.isEmpty) PasswordStrengthBar("", "", "0%")
      else passScore match{
        case 0 => PasswordStrengthBar("It this password for real?", "red accent-4", "20%")
        case 1 => PasswordStrengthBar("Bad choice", "orange darken-2", "40%")
        case 2 => PasswordStrengthBar("Better than nothing...", "yellow lighten-1", "60%")
        case 3 => PasswordStrengthBar("Strong one!", "light-green accent-2", "80%")
        case 4 => PasswordStrengthBar("Good choice!", "light-green accent-4", "100%")
      }
    }

    def render(p: Props, s: State): VdomElement = {
      val passwordStrengthInfo = passwordStrength(s)
      <.div(^.className:="row",
        loadingModal(),
        <.form(
          ^.paddingTop:="7vh",
          ^.onSubmit ==> verifyPassword(p, s),
          <.div(^.className:="col s12 m6 offset-m3 l4 offset-l4",
            <.div(^.className:="card center-align mg",
              <.div(^.className:="card-content",
                BrandLogo(),
                <.div(^.padding:="1vh"),
                s.errorMsg.whenDefined(msg => <.div(^.className:="red-text ", msg)),
                <.div(
                  ^.className:="input-field",
                  <.input(
                    ^.id:="email",
                    ^.`type`:="email",
                    ^.value:=s.email,
                    ^.className:="validate",
                    ^.required:=true,
                    ^.onChange ==> ((e: ReactEventFromInput) => onEmailChange(e, s))
                  ),
                  <.label(^.`for`:="email", ^.className:="active", "E-mail")
                ),

                PasswordField(s.password, onPasswordChange(s)),

                <.div(^.className:="input-field",
                  <.input(
                    ^.id:="cPassword",
                    ^.`type`:="password",
                    ^.value:=s.cPassword,
                    ^.className:=classCPassword(s),
                    ^.required:=true,
                    ^.onChange ==> ((e: ReactEventFromInput) => onCPasswordChange(e, s))
                  ),
                  <.label(^.`for`:="cPassword", ^.className:="active", "Confirm password"),
                  <.span(
                    ^.className:="helper-text",
                    VdomAttr("data-error"):="Passwords do not match",
                    VdomAttr("data-success"):="Passwords match")
                ),
                passwordStrengthInfo.msg,
                  <.div(^.className:="progress",
                  <.div(^.className:=s"determinate black ${passwordStrengthInfo.color}", ^.width:=passwordStrengthInfo.progress),
                ),
                  <.button(
                  ^.className:="btn cyan accent-4 white-text waves-effect waves",
                  ^.`type`:="submit",
                  "Sign Up",
                  <.i(^.className:="material-icons right", "send"))
              )
            )
          )
        ),

        <.div(^.className:="col s12 m6 offset-m3 l4 offset-l4",
          <.div(^.className:="card center-align mg",
            <.div(^.className:="card-content", "Already have account?"),
            <.div(^.className:="card-action",
                <.button(
                  p.router.setOnClick(SignInPage),
                  ^.className:="btn cyan accent-4 white-text waves-effect waves", "Sign in", <.i(^.className:="material-icons right", "star")))
            )
        )
      )
    }
  }

  val component = ScalaComponent.builder[Props]("LoginForm")
    .initialState( State( ) )
    .renderBackend[Backend]
    .build

  def apply(router: RouterCtl[Pages]) = component(Props(router))

}
