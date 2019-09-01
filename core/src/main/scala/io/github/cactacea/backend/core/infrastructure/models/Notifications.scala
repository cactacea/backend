package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, NotificationId}

case class Notifications (
                           id: NotificationId,
                           userId: UserId,
                           by: UserId,
                           notificationType: NotificationType,
                           contentId: Option[Long],
                           url: String,
                           unread: Boolean,
                           notifiedAt: Long
                         )
