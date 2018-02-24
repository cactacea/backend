package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.GroupInvite
import io.github.cactacea.core.infrastructure.models._

object GroupInviteFactory {

  def create(gi: GroupInvites, u1: Accounts, r1: Option[Relationships], g: Groups, m: Option[Messages], um: Option[AccountMessages], u2: Option[Accounts], r2: Option[Relationships]): GroupInvite = {
    val by = AccountFactory.create(u1, r1)
    val group = GroupFactory.create(g, m, um, u2, r2)
    GroupInvite(
      id            = gi.id,
      group         = group,
      by            = by,
      inviteStatus  = gi.inviteStatus,
      invitedAt     = gi.invitedAt
    )
  }

}
