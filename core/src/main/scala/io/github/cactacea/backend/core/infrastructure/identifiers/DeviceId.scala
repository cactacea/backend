package io.github.cactacea.backend.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class DeviceId(val value: Long) extends WrappedValue[Long]
