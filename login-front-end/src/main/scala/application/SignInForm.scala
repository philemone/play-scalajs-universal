package application

import apiservice.AjaxApiClient
import japgolly.scalajs.react.vdom.html_<^._
import shared.api.WebApi
import autowire._
import scala.concurrent.ExecutionContext.Implicits.global
import boopickle.Default._
import components.PasswordField
import facades.MD5
import japgolly.scalajs.react.extra.router.RouterCtl
import router.{Pages, RedirectMain, SignUpPage}
import shared.models.{ClientUser, LoginUserError}
import wvlet.log.LogSupport
import japgolly.scalajs.react._
import vdom.html_<^._


object SignInForm extends LogSupport {

  case class Props(router: RouterCtl[Pages])
  case class State(email: String = "", password: String = "", errorMsg: Option[String] = None)

  protected class Backend(val $: BackendScope[Props, State]) {

    def onEmailChange(e: ReactEventFromInput, s: State) = {
      $.setState(s.copy(email = e.target.value))
    }

    def onPasswordChange(s: State) = (e: ReactEventFromInput) => {
      $.setState(s.copy(password = e.target.value))
    }

    def md5Hash(s: String) = MD5.md5(s)
    def signIn(p: Props, s: State)(e: ReactEventFromInput) = {
        def apiCall = AjaxApiClient[WebApi].signIn(s.email, md5Hash(s.password)).call().map {
          case Right(_: ClientUser) => p.router.set(RedirectMain).runNow()
          case Left(lue: LoginUserError) => $.modState(st => st.copy(password = "", errorMsg = Some(lue.getMessage))).runNow()
        }
        e.preventDefaultCB >> Callback(apiCall)
    }

    def render(p: Props, s: State): VdomElement = {
        <.div(^.className:="row",
          <.form(
            ^.paddingTop:="5vh",
            ^.onSubmit ==> signIn(p, s),
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
                      <.label(^.`for`:="email", ^.className:="active", "Email")
                    ),

                    PasswordField(s.password, onPasswordChange(s)),

                  <.button(
                    ^.className:="btn cyan accent-4 white-text waves-effect waves",
                    ^.`type`:="submit",
                    "Sign In",
                    <.i(^.className:="material-icons right", "send"))
                    )
                  )
                )
              ),

        <.div(^.className:="col s12 m6 offset-m3 l4 offset-l4",
          <.div(^.className:="card center-align mg",
            <.div(^.className:="card-content", "Do not have account yet? Sign up now!"),
            <.div(^.className:="card-action",
                <.button(
                  p.router.setOnClick(SignUpPage),
                  ^.className:="btn cyan accent-4 white-text waves-effect waves", "Sign up", <.i(^.className:="material-icons right", "star")))
          ))
        )
    }
  }


  val component = ScalaComponent.builder[Props]("SignInForm")
    .initialState( State( ) )
    .renderBackend[Backend]
    .build

  def apply(router: RouterCtl[Pages]) = component(Props(router))

}
