package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{GroupId, GroupInviteId, AccountId}

case class GroupInvites(
                         id: GroupInviteId,
                         groupId: GroupId,
                         accountId: AccountId,
                         by: AccountId,
                         notified: Boolean,
                         inviteStatus: Long,
                         invitedAt: Long
                   )
