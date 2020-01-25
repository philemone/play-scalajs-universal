package router


sealed trait Pages{
  def name: String
}

case object CoolPage extends Pages{
  def name: String = "Cool Page"
}

case object AmazingPage         extends Pages{
  def name: String = "Amazing Page"
}

case object MarvelousPage              extends Pages{
  def name: String = "Marvelous Page"
}

case object AccountSettingPage          extends Pages{
  def name: String = "Account Setting"
}

case object RedirectLoginPage           extends Pages{
  def name: String = "Redirect Login"
}

case class CardInfoPage(id: String)    extends Pages{
  def name: String = "Redirect Login"
}

