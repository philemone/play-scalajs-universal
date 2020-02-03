package router

import application.menucomp.{AmazingPageComp, CoolPageComp, MarvelousPageComp}
import application.useraccountsetting.UserAccountSettingPanel
import application.{LeftSidenav, Navbar}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._

object RouterConfiguration {

  val config = RouterConfigDsl[Pages].buildConfig { dsl =>
    import dsl._

    (emptyRule
      | staticRoute("#CoolPage", CoolPage) ~> renderR(r => CoolPageComp(r))
      | staticRoute("#AmazingPage", AmazingPage) ~> renderR(r => AmazingPageComp(r))
      | staticRoute("#MarvelousPage", MarvelousPage) ~> renderR(r => MarvelousPageComp(r))
      | staticRoute("#AccountSetting",  AccountSettingPage)  ~> render(UserAccountSettingPanel())
      | staticRoute("#RedirectLogin",  RedirectLoginPage) ~> redirectToPath("login")(Redirect.Force)
      ) .notFound(_ => redirectToPage(AmazingPage)(Redirect.Force))
      .setTitle(p => s"${p.name} | PawsCode").renderWith(layout)
  }

  def layout(routerCtl: RouterCtl[Pages], resolution: Resolution[Pages]) = {
    <.div(
    <.header(
      <.nav(^.id:="Navbar", Navbar()),
      <.div(^.id:="LeftSidenav", LeftSidenav(routerCtl))
      ),
      <.main(
        <.div(^.className:="",
          <.div(^.className:="row",
            <.div(^.className:="col s12 m12 l12",
               <.div(^.className:="section scrollspy", resolution.render())
            )
          )
        )
      )
    )
  }
}
