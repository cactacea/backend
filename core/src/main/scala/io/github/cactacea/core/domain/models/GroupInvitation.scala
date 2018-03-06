package io.github.cactacea.core.domain.models

import io.github.cactacea.core.domain.enums.GroupInvitationStatusType
import io.github.cactacea.core.infrastructure.identifiers.GroupInvitationId
import io.github.cactacea.core.infrastructure.models._

case class GroupInvitation(
                            id: GroupInvitationId,
                            group: Group,
                            by: Account,
                            invitationStatus: GroupInvitationStatusType,
                            invitedAt: Long
                   )

object GroupInvitation {

  def apply(gi: GroupInvitations, u1: Accounts, r1: Option[Relationships], g: Groups, m: Option[Messages], um: Option[AccountMessages], u2: Option[Accounts], r2: Option[Relationships]): GroupInvitation = {
    val by = Account(u1, r1)
    val group = Group(g, m, um, u2, r2)
    GroupInvitation(
      id            = gi.id,
      group         = group,
      by            = by,
      invitationStatus  = gi.invitationStatus,
      invitedAt     = gi.invitedAt
    )
  }

}

