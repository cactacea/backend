package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.GroupInvitationStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupInvitationId
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, GroupInvitations, _}

case class GroupInvitation(
                            id: GroupInvitationId,
                            group: Group,
                            by: Account,
                            invitationStatus: GroupInvitationStatusType,
                            invitedAt: Long
                   )

object GroupInvitation {

  //(gi: GroupInvitations, a1: Accounts, r1: Option[Relationships], g: Groups, m: Option[Messages], um: Option[AccountMessages], a2: Option[Accounts], r2: Option[Relationships]): GroupInvitation = {
  def apply(gi: GroupInvitations, a: Accounts, r: Option[Relationships], g: Groups): GroupInvitation = {
    val by = Account(a, r)
    val group = Group(g, None, None, None, None)
    GroupInvitation(
      id                = gi.id,
      group             = group,
      by                = by,
      invitationStatus  = gi.invitationStatus,
      invitedAt         = gi.invitedAt
    )
  }

}


