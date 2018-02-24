package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.{GroupId, MessageId, AccountId}

case class SessionGroup(
                         id: GroupId,
                         name: Option[String],
                         groupPrivacyType: Long,
                         byInvitationOnly: Boolean,
                         authorityType: Long,
                         accountCount: Long,
                         messageId: Option[MessageId],
                         by: AccountId,
                         oneToOneAccountId: AccountId,
                         next: Long
                 )
