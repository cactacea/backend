package io.github.cactacea.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.core.domain.models.PushNotification
import io.github.cactacea.core.infrastructure.identifiers._

trait PushNotificationService {
  def send(fanOuts: List[PushNotification]): Future[List[AccountId]]
}
