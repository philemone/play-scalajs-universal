package router

sealed trait Pages{
  val name: String
}

case object SignInPage               extends Pages{
  val name: String = "Sign In"
}

case object SignUpPage               extends Pages{
  val name: String = "Sign Up"
}

case object RedirectMain             extends Pages{
  val name: String = "Redirecting..."
}
