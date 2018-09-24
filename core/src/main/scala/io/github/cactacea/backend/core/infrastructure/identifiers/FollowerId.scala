package io.github.cactacea.backend.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class FollowerId(val value: Long) extends WrappedValue[Long]
