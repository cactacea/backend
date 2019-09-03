package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers.InvitationId
import io.github.cactacea.backend.core.infrastructure.models.{Users, Invitations, _}

case class Invitation(
                       id: InvitationId,
                       channel: Channel,
                       user: User,
                       invitedAt: Long,
                       next: Long
                   )

object Invitation {

  def apply(gi: Invitations, a: Users, r: Option[Relationships], g: Channels, next: Long): Invitation = {
    val user = User(a, r)
    val channel = Channel(g)
    Invitation(
      id                = gi.id,
      channel             = channel,
      user           = user,
      invitedAt         = gi.invitedAt,
      next              = next
    )
  }

}
