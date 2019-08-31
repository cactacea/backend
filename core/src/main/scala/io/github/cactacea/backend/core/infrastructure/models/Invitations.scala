package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, ChannelId, InvitationId}

case class Invitations(
                        id: InvitationId,
                        channelId: ChannelId,
                        accountId: AccountId,
                        by: AccountId,
                        notified: Boolean,
                        invitedAt: Long
                   )
