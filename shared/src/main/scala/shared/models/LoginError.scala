package shared.models

sealed trait LoginUserError extends Exception{
  def getMessage: String
}

case class UserExist(login: String) extends LoginUserError {
  override def getMessage = s"User $login already exists"
}

case class IncorrectCredentials() extends LoginUserError {
  override def getMessage = s"Incorrect email or password"
}

case class WrongCredentials(msg: String) extends LoginUserError{
  override def getMessage = s"$msg"
}

case class UnknownUserError(msg: String) extends LoginUserError{
  override def getMessage = s"$msg"
}

case class NoErrors(msg: String) extends LoginUserError{
  override def getMessage = s"$msg"
}
