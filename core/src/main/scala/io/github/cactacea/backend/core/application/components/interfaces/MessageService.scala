package io.github.cactacea.backend.core.application.components.interfaces

import java.util.Locale

import io.github.cactacea.backend.core.domain.enums.{NotificationType, PushNotificationType}

trait MessageService {

  def getPushNotificationMessage(pushNotificationType: PushNotificationType, locales: Seq[Locale], args : Any*): String
  def getNotificationMessage(notificationType: NotificationType, locales: Seq[Locale], args : Any*): String

}
