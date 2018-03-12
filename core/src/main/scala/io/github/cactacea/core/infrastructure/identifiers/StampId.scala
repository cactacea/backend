package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class StampId(val value: Long) extends WrappedValue[Long]
