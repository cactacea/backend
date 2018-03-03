package io.github.cactacea.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.core.domain.models.PushNotification

trait NotificationService {
  def send(feeds: List[PushNotification]): Future[Boolean]
}
