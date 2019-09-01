package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{ChannelPrivacyType, ChannelAuthorityType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, ChannelId, MessageId}

case class Channels(
                     id: ChannelId,
                     name: Option[String],
                     privacyType: ChannelPrivacyType,
                     invitationOnly: Boolean,
                     directMessage: Boolean,
                     authorityType: ChannelAuthorityType,
                     userCount: Long,
                     messageId: Option[MessageId],
                     by: UserId,
                     lastPostedAt: Option[Long],
                     organizedAt: Long
                  )
