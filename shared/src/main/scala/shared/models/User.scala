package shared.models

case class User(_id: String,
                email: String,
                password: String,
                sessionId: String = "",
                avatarSrc: Option[String] = None
               ) {

  def clientUser: ClientUser = ClientUser(email, sessionId, avatarSrc)
}

