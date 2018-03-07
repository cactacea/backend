package io.github.cactacea.core.application.components.thirdparties.onesignal

import com.google.inject.Inject
import com.osinka.i18n.Lang
import com.twitter.util.{Future, Return, Throw}
import io.github.cactacea.core.application.components.interfaces.{NotificationMessagesService, PushNotificationService}
import io.github.cactacea.core.domain.models.PushNotification
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.util.clients.onesignal.{OneSignalClient, OneSignalNotification}

class OneSignalService extends PushNotificationService {

  @Inject private var client: OneSignalClient = _
  @Inject private var messageService: NotificationMessagesService = _

  def send(fanOuts: List[PushNotification]): Future[List[AccountId]] = {
    val notifications = fanOuts.flatMap({ fanOut =>
      fanOut.tokens.map(_._2).grouped(2000).map({ tokens =>
        val displayName = fanOut.displayName
        val accountIds = fanOut.tokens.map(_._1)
        val message = fanOut.message.getOrElse("")
        val en = messageService.get(fanOut.pushNotificationType, Lang("en"), displayName, message)
        val jp = messageService.get(fanOut.pushNotificationType, Lang("en"), displayName, message)
        val notification = OneSignalNotification(client.appId, tokens, en, jp)
        (notification, accountIds)
      })
    })
    Future.traverseSequentially(notifications) { case (notification, accountIds) =>
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
