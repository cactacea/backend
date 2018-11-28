package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, MessageId}

case class Groups(
                   id: GroupId,
                   name: Option[String],
                   privacyType: GroupPrivacyType,
                   invitationOnly: Boolean,
                   directMessage: Boolean,
                   authorityType: GroupAuthorityType,
                   accountCount: Long,
                   messageId: Option[MessageId],
                   by: AccountId,
                   lastPostedAt: Option[Long],
                   organizedAt: Long
                  )
