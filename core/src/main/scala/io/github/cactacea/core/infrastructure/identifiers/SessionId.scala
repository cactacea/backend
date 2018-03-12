package io.github.cactacea.core.infrastructure.identifiers

case class SessionId(val value: Long) extends AnyVal {
  def toAccountId: AccountId = {
    AccountId(value)
  }
}
