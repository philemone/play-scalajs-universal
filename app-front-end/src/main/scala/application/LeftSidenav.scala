package application

import apiservice.AjaxApiClient
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import shared.api.WebApi
import autowire._
import shared.models.ClientUser
import boopickle.Default._
import japgolly.scalajs.react.extra.router.RouterCtl
import org.scalajs.dom.html.LI
import router.{AccountSettingPage, AmazingPage, CoolPage, MarvelousPage, Pages, RedirectLoginPage}
import scala.concurrent.ExecutionContext.Implicits.global

object LeftSidenav {

  case class Props(router: RouterCtl[Pages])
  case class State(user: ClientUser)

  def signOut(p: Props)(e: ReactEventFromInput) = {
    def apiCall = AjaxApiClient[WebApi].signOut().call().map(_ => p.router.set(RedirectLoginPage).runNow())
    e.preventDefaultCB >> Callback(apiCall)
  }

  class Backend($: BackendScope[Props, State]) {

    lazy val defaultAvatar = "assets/avatars/cat_3.png"

    def menuBtn(p: Props, page: Pages, text: String, extraClass: String="", signOutAction: Boolean = false): VdomTagOf[LI] = if(signOutAction)
      <.li(<.a(^.className:=s"waves-effect waves $extraClass", ^.href:="#", ^.onClick ==> signOut(p), text))
    else  <.li(<.a(^.className:=s"waves-effect waves $extraClass", ^.href:="#", p.router.setOnLinkClick(page), text))


    def render(p: Props, s: State) = {
      <.ul(^.id:="leftSidenav", ^.className:="sidenav sidenav-fixed",
        <.li(
          <.div(^.className:="user-view",
            <.div(^.className:="background", <.img(
              ^.className:="responsive-img",
              ^.src:="assets/images/client-bg.jpg")),
            <.a(^.href:="#user", <.img(^.className:="circle z-depth-2", ^.src:=s"${s.user.avatarSrc.getOrElse(defaultAvatar)}")),
            <.a(^.href:="#name",<.span(^.className:="white-text name", s"${s.user.email}")),
            <.a(^.href:="#email", <.span(^.className:="white-text email", s""))
            )
        ),
        <.li(<.a(^.className:="subheader z-depth-1", "Left panel menu")),
        menuBtn(p, CoolPage, "Cool Page"),
        menuBtn(p, AmazingPage, "Amazing Page"),
        menuBtn(p, MarvelousPage, "Marvelous Page"),
        <.li(^.className:="divider"),
        <.li(<.a(^.className:="subheader z-depth-1", "User settings")),
        menuBtn(p, AccountSettingPage, "Account setting"),
        menuBtn(p, RedirectLoginPage, "Sign out", "right-align", true),
      )
    }
  }

  val component = ScalaComponent.builder[Props]("LeftSidenav")
    .initialStateFromProps(_ => {
      State(ClientUser("Loading...", "Loading..."))
    })
    .renderBackend[Backend]
    .componentDidMount($ => {
        Callback{ AjaxApiClient[WebApi].getLoggedUser().call().map(out => {
          $.modState(state => state.copy(user = out.getOrElse(state.user))).runNow()
        })
      }
    })
    .build

  def apply(router: RouterCtl[Pages]) = component(Props(router))

}
