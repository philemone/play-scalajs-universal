package shared.models

case class ClientUser(email: String, sessionId: String, avatarSrc: Option[String] = None)

