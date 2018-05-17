package io.github.cactacea.backend.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class AccountGroupId(val value: Long) extends WrappedValue[Long]