package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserChannelId, UserId, ChannelId}

case class UserChannels(
                            id: UserChannelId,
                            userId: UserId,
                            channelId: ChannelId,
                            unreadCount: Long,
                            hidden: Boolean,
                            mute: Boolean,
                            by: UserId,
                            joinedAt: Long
                        )
