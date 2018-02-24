package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class CommentId(val value: Long) extends AnyVal with WrappedValue[Long]
