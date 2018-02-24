package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.{GroupInviteId}

case class GroupInvite(
                        id: GroupInviteId,
                        group: Group,
                        by: Account,
                        inviteStatus: Long,
                        invitedAt: Long
                   )
