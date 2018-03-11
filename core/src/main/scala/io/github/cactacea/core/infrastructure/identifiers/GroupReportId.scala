package io.github.cactacea.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class GroupReportId(val value: Long) extends WrappedValue[Long]
