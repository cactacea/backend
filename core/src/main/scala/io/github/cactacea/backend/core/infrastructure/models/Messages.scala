package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers._

case class Messages(
                     id: MessageId,
                     by: AccountId,
                     groupId: GroupId,
                     messageType: MessageType,
                     message: Option[String],
                     mediumId: Option[MediumId],
                     stampId: Option[StampId],
                     accountCount: Long,
                     readAccountCount: Long,
                     contentWarning: Boolean,
                     contentStatus: ContentStatusType,
                     notified: Boolean,
                     postedAt: Long
               )