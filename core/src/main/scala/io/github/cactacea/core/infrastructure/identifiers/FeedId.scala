package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class FeedId(val value: Long) extends AnyVal with WrappedValue[Long]
