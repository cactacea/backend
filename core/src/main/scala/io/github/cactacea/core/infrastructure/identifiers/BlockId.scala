package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class BlockId(val value: Long) extends WrappedValue[Long]
