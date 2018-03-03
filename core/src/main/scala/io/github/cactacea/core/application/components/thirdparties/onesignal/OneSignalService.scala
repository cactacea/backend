package io.github.cactacea.core.application.components.thirdparties.onesignal

import com.google.inject.Inject
import com.twitter.util.{Future, Return, Throw}
import io.github.cactacea.core.application.components.interfaces.{MessageService, PushNotificationService}
import io.github.cactacea.core.domain.models.PushNotification
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.util.clients.onesignal.{OneSignalClient, OneSignalNotification}

class OneSignalService extends PushNotificationService {

  @Inject var client: OneSignalClient = _
  @Inject var messageService: MessageService = _

  def send(fanOuts: List[PushNotification]): Future[List[AccountId]] = {
    Future.traverseSequentially(fanOuts) { fanOut =>
      val accountIds = fanOut.tokens.map(_._1)
      val playerIds = fanOut.tokens.map(_._2)
      val message = fanOut.message
      val notification = OneSignalNotification(client.appId, playerIds, message)
      client.createNotification(notification).transform {
        case Return(response) =>
          if (response.statusCode >= 200 && response.statusCode <= 299) {
            Future.value(accountIds)
          } else {
            Future.value(List[AccountId]())
          }
        case Throw(_) =>
          Future.value(List[AccountId]())
      }
    }.map(_.flatten.toList.distinct)
  }

}
