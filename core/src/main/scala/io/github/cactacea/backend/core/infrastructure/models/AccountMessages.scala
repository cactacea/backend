package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, MessageId, AccountId}

case class AccountMessages(
                            accountId: AccountId,
                            channelId: ChannelId,
                            messageId: MessageId,
                            by: AccountId,
                            unread: Boolean,
                            notified: Boolean,
                            postedAt: Long
                    )
