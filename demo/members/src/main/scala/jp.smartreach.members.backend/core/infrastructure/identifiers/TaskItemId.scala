package jp.smartreach.members.backend.core.infrastructure.identifiers

import com.twitter.inject.domain.WrappedValue

case class TaskItemId(val value: Long) extends WrappedValue[Long]
