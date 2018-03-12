package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class GroupId(val value: Long) extends WrappedValue[Long]
