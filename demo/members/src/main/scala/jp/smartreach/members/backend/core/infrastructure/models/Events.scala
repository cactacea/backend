package jp.smartreach.members.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId
import jp.smartreach.members.backend.core.infrastructure.identifiers.{AdjustmentId, EventId, GroupId, MemberId, TaskId}

case class Events (
                    id: EventId,
                    name: String,
                    description: String,
                    latlng: Option[String],
                    adjustmentId: Option[AdjustmentId],
                    taskId: Option[TaskId],
                    channelId: ChannelId,
                    groupId: GroupId,
                    by: MemberId,
                    createdAt: Long
                  )
