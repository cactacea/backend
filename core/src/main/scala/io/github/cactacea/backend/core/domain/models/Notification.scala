package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.infrastructure.identifiers.NotificationId
import io.github.cactacea.backend.core.infrastructure.models.Notifications

case class Notification (
                           id: NotificationId,
                           notificationType: NotificationType,
                           contentId: Option[Long],
                           message: String,
                           url: String,
                           notifiedAt: Long
                         )

object Notification {

  def apply(n: Notifications, message: String): Notification = {
    new Notification(
      n.id,
      n.notificationType,
      n.contentId,
      message,
      n.url,
      n.notifiedAt
    )
  }

}
