package io.github.cactacea.backend.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class UserId(val value: Long) extends WrappedValue[Long] {
  def sessionId: SessionId = {
    SessionId(value)
  }
}
