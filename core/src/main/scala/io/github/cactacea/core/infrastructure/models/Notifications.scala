package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.NotificationType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, NotificationId}

case class Notifications (
                           id: NotificationId,
                           accountId: AccountId,
                           notificationType: NotificationType,
                           contentId: Option[Long],
                           message: Option[String],
                           url: Option[String],
                           unread: Boolean,
                           notifiedAt: Long
                         )
