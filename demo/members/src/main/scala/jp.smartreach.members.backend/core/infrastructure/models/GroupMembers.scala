package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.{GroupId, MemberId, PositionId}

case class GroupMembers(
                         groupId: GroupId,
                         memberId: MemberId,
                         positionId: PositionId,
                         joinedAt: Long
                       )
