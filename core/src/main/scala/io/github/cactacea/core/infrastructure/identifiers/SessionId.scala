package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class SessionId(val value: Long) extends WrappedValue[Long] {
  def toAccountId: AccountId = {
    AccountId(value)
  }
}
