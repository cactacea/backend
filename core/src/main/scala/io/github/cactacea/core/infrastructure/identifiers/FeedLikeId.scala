package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class FeedLikeId(val value: Long) extends WrappedValue[Long]
