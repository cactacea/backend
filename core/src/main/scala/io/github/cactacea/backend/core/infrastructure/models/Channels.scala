package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{ChannelPrivacyType, ChannelAuthorityType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, ChannelId, MessageId}

case class Channels(
                     id: ChannelId,
                     name: Option[String],
                     privacyType: ChannelPrivacyType,
                     invitationOnly: Boolean,
                     directMessage: Boolean,
                     authorityType: ChannelAuthorityType,
                     accountCount: Long,
                     messageId: Option[MessageId],
                     by: AccountId,
                     lastPostedAt: Option[Long],
                     organizedAt: Long
                  )
