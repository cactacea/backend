package jp.smartreach.members.backend.core.infrastructure.models

import io.smartreach.members.backend.core.domain.enums.TaskStatusType
import jp.smartreach.members.backend.core.infrastructure.identifiers.{MemberId, TaskId, TaskItemId}

case class TaskItems (
                       id: TaskItemId,
                       title: String,
                       description: String,
                       personAssigned: String,
                       status: TaskStatusType,
                       taskId: TaskId,
                       by: MemberId,
                       createdAt: Long
                     )
