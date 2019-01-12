package io.github.cactacea.backend.core.application.components.services

import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.MobilePushService
import io.github.cactacea.backend.core.domain.models.PushNotification
import io.github.cactacea.backend.core.infrastructure.identifiers._

class DefaultMobilePushService extends MobilePushService {

  def send(fanOuts: List[PushNotification]): Future[List[AccountId]] = {
    Future.value(fanOuts.map(_.tokens.map({ case (accountId, _) => accountId })).flatten.distinct)
  }

}
