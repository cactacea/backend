package io.github.cactacea.backend.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class AccountId(val value: Long) extends WrappedValue[Long] {
  def toSessionId = {
    SessionId(value)
  }
}
