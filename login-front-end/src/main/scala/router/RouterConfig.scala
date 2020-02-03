package router

import application.{SignInForm, SignUpForm}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._

object RouterConfiguration {

  val config = RouterConfigDsl[Pages].buildConfig { dsl =>
    import dsl._

    (emptyRule
      | staticRoute("login#signin", SignInPage) ~> renderR(SignInForm(_))
      | staticRoute("login#signup", SignUpPage) ~> renderR(SignUpForm(_))
      | staticRoute("#", RedirectMain) ~> redirectToPath("")(Redirect.Force)
    ).notFound(redirectToPage(SignInPage)(Redirect.Push))
     .setTitle(p => s"${p.name} | PawsCode")
  }

}
