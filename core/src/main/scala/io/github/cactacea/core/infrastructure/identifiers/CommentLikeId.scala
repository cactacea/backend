package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class CommentLikeId(val value: Long) extends WrappedValue[Long]
