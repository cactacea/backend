package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, MessageId, AccountId}

case class AccountMessages(
                            accountId: AccountId,
                            groupId: GroupId,
                            messageId: MessageId,
                            by: AccountId,
                            unread: Boolean,
                            notified: Boolean,
                            postedAt: Long
                    )
