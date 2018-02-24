package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class CommentReportId(val value: Long) extends AnyVal with WrappedValue[Long]
