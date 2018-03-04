package io.github.cactacea.core.domain.models

import io.github.cactacea.core.domain.enums.NotificationType
import io.github.cactacea.core.infrastructure.identifiers.NotificationId
import io.github.cactacea.core.infrastructure.models.Notifications

case class Notification (
                           id: NotificationId,
                           notificationType: NotificationType,
                           contentId: Option[Long],
                           message: Option[String],
                           url: Option[String],
                           notifiedAt: Long
                         )

object Notification {

  def apply(n: Notifications): Notification = {
    Notification(
      n.id,
      n.notificationType,
      n.contentId,
      n.message,
      n.url,
      n.notifiedAt
    )
  }

}