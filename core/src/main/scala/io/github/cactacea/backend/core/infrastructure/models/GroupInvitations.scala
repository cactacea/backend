package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.GroupInvitationStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, GroupInvitationId}

case class GroupInvitations(
                             id: GroupInvitationId,
                             groupId: GroupId,
                             accountId: AccountId,
                             by: AccountId,
                             notified: Boolean,
                             invitationStatus: GroupInvitationStatusType,
                             invitedAt: Long
                   )
