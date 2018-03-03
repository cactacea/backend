package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{GroupId, GroupInvitationId, AccountId}

case class GroupInvitations(
                             id: GroupInvitationId,
                             groupId: GroupId,
                             accountId: AccountId,
                             by: AccountId,
                             notified: Boolean,
                             invitationStatus: Long,
                             invitedAt: Long
                   )
