package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers.InvitationId
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Invitations, _}

case class Invitation(
                            id: InvitationId,
                            group: Group,
                            account: Account,
                            invitedAt: Long,
                            next: Long
                   )

object Invitation {

  def apply(gi: Invitations, a: Accounts, r: Option[Relationships], g: Groups, next: Long): Invitation = {
    val account = Account(a, r)
    val group = Group(g)
    Invitation(
      id                = gi.id,
      group             = group,
      account           = account,
      invitedAt         = gi.invitedAt,
      next              = next
    )
  }

}
