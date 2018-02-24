package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{GroupId, MessageId, AccountId}

case class Groups(
                   id: GroupId,
                   name: Option[String],
                   privacyType: Long,
                   byInvitationOnly: Boolean,
                   isDirectMessage: Boolean,
                   authorityType: Long,
                   accountCount: Long,
                   messageId: Option[MessageId],
                   by: AccountId,
                   organizedAt: Long
                  )
