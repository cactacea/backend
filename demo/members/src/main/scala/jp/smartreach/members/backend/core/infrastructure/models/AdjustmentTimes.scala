package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.{AdjustmentId, AdjustmentTimeId}

case class AdjustmentTimes (
                             id: AdjustmentTimeId,
                             title: String,
                             adjustmentId: AdjustmentId
                           )
