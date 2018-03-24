package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.identifiers._

trait PushNotificationService {
  def send(fanOuts: List[PushNotification]): Future[List[AccountId]]
}
