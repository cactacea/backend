package io.github.cactacea.core.application.components.services

import com.google.inject.Singleton
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.NotificationService
import io.github.cactacea.core.domain.models.PushNotification

@Singleton
class DefaultNotificationService extends NotificationService {

  def send(feeds: List[PushNotification]): Future[Boolean] = {
    Future.True
  }

}
