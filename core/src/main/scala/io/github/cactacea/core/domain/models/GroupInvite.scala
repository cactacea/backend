package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.GroupInviteId
import io.github.cactacea.core.infrastructure.models._

case class GroupInvite(
                        id: GroupInviteId,
                        group: Group,
                        by: Account,
                        inviteStatus: Long,
                        invitedAt: Long
                   )

object GroupInvite {

  def apply(gi: GroupInvites, u1: Accounts, r1: Option[Relationships], g: Groups, m: Option[Messages], um: Option[AccountMessages], u2: Option[Accounts], r2: Option[Relationships]): GroupInvite = {
    val by = Account(u1, r1)
    val group = Group(g, m, um, u2, r2)
    GroupInvite(
      id            = gi.id,
      group         = group,
      by            = by,
      inviteStatus  = gi.inviteStatus,
      invitedAt     = gi.invitedAt
    )
  }

}

