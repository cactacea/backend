package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class NotificationId(val value: Long) extends WrappedValue[Long]
