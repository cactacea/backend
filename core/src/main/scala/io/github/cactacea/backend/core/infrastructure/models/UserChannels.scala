package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ChannelAuthorityType
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, UserChannelId, UserId}

case class UserChannels(
                            id: UserChannelId,
                            userId: UserId,
                            channelId: ChannelId,
                            unreadCount: Long,
                            hidden: Boolean,
                            mute: Boolean,
                            authorityType: ChannelAuthorityType,
                            by: UserId,
                            joinedAt: Long
                        )
