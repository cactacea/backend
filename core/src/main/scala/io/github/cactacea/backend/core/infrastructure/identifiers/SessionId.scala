package io.github.cactacea.backend.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class SessionId(val value: Long) extends WrappedValue[Long] {
  def userId: UserId = {
    UserId(value)
  }
}
