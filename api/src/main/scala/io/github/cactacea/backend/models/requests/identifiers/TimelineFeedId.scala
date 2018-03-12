package io.github.cactacea.backend.models.requests.identifiers

import com.twitter.inject.domain.WrappedValue

case class TimelineFeedId(val value: Long) extends WrappedValue[Long]
