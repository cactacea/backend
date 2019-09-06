package jp.smartreach.members.backend.core.infrastructure.models

import io.smartreach.members.backend.core.domain.enums.EventJoinType
import jp.smartreach.members.backend.core.infrastructure.identifiers.{EventId, MemberId}

case class EventMembers (
                          eventId: EventId,
                          memberId: MemberId,
                          joinType: EventJoinType
                        )
