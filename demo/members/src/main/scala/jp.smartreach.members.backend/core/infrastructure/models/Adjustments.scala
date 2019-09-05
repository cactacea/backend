package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.{AdjustmentId, GroupId, MemberId}

case class Adjustments (
                         id: AdjustmentId,
                         title: String,
                         description: String,
                         groupId: GroupId,
                         by: MemberId,
                         createdAt: Long
                       )
