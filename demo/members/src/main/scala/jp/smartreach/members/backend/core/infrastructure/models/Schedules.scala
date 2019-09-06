package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.{EventId, ScheduleId}

case class Schedules (
                       id: ScheduleId,
                       title: String,
                       description: String,
                       scheduledDate: Long,
                       eventId: Option[EventId]
                     )
