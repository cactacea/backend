package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.MessageId

case class Message(
                    id: MessageId,
                    messageType: Long,
                    message: Option[String],
                    medium: Option[Medium],
                    by: Account,
                    account: Option[Account],
                    unread: Boolean,
                    accountCount: Long,
                    readAccountCount: Long,
                    postedAt: Long
                  )
