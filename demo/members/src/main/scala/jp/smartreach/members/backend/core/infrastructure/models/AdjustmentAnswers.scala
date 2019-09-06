package jp.smartreach.members.backend.core.infrastructure.models

import io.smartreach.members.backend.core.domain.enums.AdjustmentAnswerType
import jp.smartreach.members.backend.core.infrastructure.identifiers.{AdjustmentId, AdjustmentTimeId, MemberId}

case class AdjustmentAnswers (
                               adjustmentId: AdjustmentId,
                               adjustmentTimeId: AdjustmentTimeId,
                               memberId: MemberId,
                               answer: AdjustmentAnswerType,
                               createdAt: Long
                             )
