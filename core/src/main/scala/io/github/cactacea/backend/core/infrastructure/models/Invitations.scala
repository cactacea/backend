package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, ChannelId, InvitationId}

case class Invitations(
                        id: InvitationId,
                        channelId: ChannelId,
                        userId: UserId,
                        by: UserId,
                        notified: Boolean,
                        invitedAt: Long
                   )
