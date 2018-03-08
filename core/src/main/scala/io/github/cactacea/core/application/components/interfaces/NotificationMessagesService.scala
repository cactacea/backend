package io.github.cactacea.core.application.components.interfaces

import java.util.Locale

import io.github.cactacea.core.domain.enums.{NotificationType, PushNotificationType}

trait NotificationMessagesService {

  def getPushNotificationMessage(pushNotificationType: PushNotificationType, locales: Seq[Locale], args : Any*): String
  def getNotificationMessage(notificationType: NotificationType, locales: Seq[Locale], args : Any*): String

}
