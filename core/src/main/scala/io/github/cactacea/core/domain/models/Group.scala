package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class Group(
                  id: GroupId,
                  name: Option[String],
                  message: Option[Message],
                  groupPrivacyType: Long,
                  byInvitationOnly: Boolean,
                  authorityType: Long,
                  accountCount: Long,
                  organizedAt: Long,
                  next: Long
                 )
