package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.GroupInvitationStatusType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, GroupInvitationId}

case class GroupInvitations(
                             id: GroupInvitationId,
                             groupId: GroupId,
                             accountId: AccountId,
                             by: AccountId,
                             notified: Boolean,
                             invitationStatus: GroupInvitationStatusType,
                             invitedAt: Long
                   )
