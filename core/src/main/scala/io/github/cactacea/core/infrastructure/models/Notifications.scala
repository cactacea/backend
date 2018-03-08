package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.NotificationType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, NotificationId}

case class Notifications (
                           id: NotificationId,
                           accountId: AccountId,
                           by: AccountId,
                           notificationType: NotificationType,
                           contentId: Option[Long],
                           url: String,
                           unread: Boolean,
                           notifiedAt: Long
                         )
