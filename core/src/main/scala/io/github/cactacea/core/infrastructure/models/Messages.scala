package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers._

case class Messages(
                     id: MessageId,
                     by: AccountId,
                     groupId: GroupId,
                     messageType: Long,
                     message: Option[String],
                     mediumId: Option[MediumId],
                     stampId: Option[StampId],
                     accountCount: Long,
                     readAccountCount: Long,
                     notified: Boolean,
                     postedAt: Long
               )