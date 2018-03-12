package io.github.cactacea.core.infrastructure.identifiers
case class AccountId(val value: Long) extends AnyVal {
  def toSessionId = {
    SessionId(value)
  }

}
