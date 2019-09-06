package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.{GroupId, MemberId}

case class Groups (
                    id: GroupId,
                    name: String,
                    memberCount: Long,
                    by: MemberId,
                    createdAt: Long,
                  )
