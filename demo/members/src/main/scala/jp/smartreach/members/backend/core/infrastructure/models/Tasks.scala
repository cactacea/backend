package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.{GroupId, MemberId, TaskId}

case class Tasks (
                   id: TaskId,
                   name: String,
                   description: String,
                   by: MemberId,
                   groupId: GroupId,
                   createdAt: Long
                 )
