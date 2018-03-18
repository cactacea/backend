package io.github.cactacea.core.application.components.services

import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.PushNotificationService
import io.github.cactacea.core.domain.models.PushNotification
import io.github.cactacea.core.infrastructure.identifiers._

class NoPushNotificationService extends PushNotificationService {

  def send(fanOuts: List[PushNotification]): Future[List[AccountId]] = {
    Future.value(fanOuts.map(_.tokens.map(_._1)).flatten.distinct)
  }

}
