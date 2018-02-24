package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MessageId, MediumId}

case class DeliveryMessage(
                            accountId: AccountId,
                            displayName: String,
                            messageId: MessageId,
                            message: Option[String],
                            mediumId: Option[MediumId],
                            postedAt: Long,
                            tokens: List[(String, AccountId)],
                            show: Boolean
                  )
