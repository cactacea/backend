package jp.smartreach.members.backend.core.infrastructure.models

import io.smartreach.members.backend.core.domain.enums.GroupAuthorityType
import jp.smartreach.members.backend.core.infrastructure.identifiers.{GroupId, MemberId, PositionId}

case class Positions (
                       id: PositionId,
                       name: String,
                       authority: GroupAuthorityType,
                       groupId: GroupId,
                       by: MemberId,
                       createdAt: Long
                     )
