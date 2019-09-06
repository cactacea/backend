package jp.smartreach.members.backend.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class AdjustmentTimeId(val value: Long) extends WrappedValue[Long]
