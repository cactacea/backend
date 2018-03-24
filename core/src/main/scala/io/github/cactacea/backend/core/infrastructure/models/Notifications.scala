package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, NotificationId}

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
