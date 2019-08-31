package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountChannelId, AccountId, ChannelId}

case class AccountChannels(
                            id: AccountChannelId,
                            accountId: AccountId,
                            channelId: ChannelId,
                            unreadCount: Long,
                            hidden: Boolean,
                            mute: Boolean,
                            by: AccountId,
                            joinedAt: Long
                        )
