package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.EventId

case class EventSchedules (
                    eventId: EventId,
                    scheduledDate: Long
                  )
