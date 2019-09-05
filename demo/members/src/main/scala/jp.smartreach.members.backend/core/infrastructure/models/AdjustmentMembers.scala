package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.{AdjustmentId, MemberId}

case class AdjustmentMembers (
                               adjustmentId: AdjustmentId,
                               memberId: MemberId,
                               comment: String,
                               createdAt: Long
                             )
