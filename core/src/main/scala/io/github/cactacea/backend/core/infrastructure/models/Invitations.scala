package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, InvitationId}

case class Invitations(
                             id: InvitationId,
                             groupId: GroupId,
                             accountId: AccountId,
                             by: AccountId,
                             notified: Boolean,
                             invitedAt: Long
                   )
