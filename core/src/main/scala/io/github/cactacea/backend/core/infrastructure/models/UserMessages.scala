package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, MessageId, UserId}

case class UserMessages(
                            userId: UserId,
                            channelId: ChannelId,
                            messageId: MessageId,
                            by: UserId,
                            unread: Boolean,
                            notified: Boolean,
                            postedAt: Long
                    )
