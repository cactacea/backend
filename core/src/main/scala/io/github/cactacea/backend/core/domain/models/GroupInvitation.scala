package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.GroupInvitationStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupInvitationId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, GroupInvitations, _}

case class GroupInvitation(
                            id: GroupInvitationId,
                            group: Group,
                            account: Account,
                            invitationStatus: GroupInvitationStatusType,
                            invitedAt: Long,
                            next: Option[Long]
                   )

object GroupInvitation {

  def apply(gi: GroupInvitations, a: Accounts, r: Option[Relationships], g: Groups, next: Long): GroupInvitation = {
    val account = Account(a, r)
    val group = Group(g)
    GroupInvitation(
      id                = gi.id,
      group             = group,
      account           = account,
      invitationStatus  = gi.invitationStatus,
      invitedAt         = gi.invitedAt,
      next              = Some(next)
    )
  }

}
