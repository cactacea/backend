package io.github.cactacea.core.application.components.interfaces

import java.util.Locale

import io.github.cactacea.core.domain.enums.PushNotificationType

trait NotificationMessagesService {

  def getPushNotification(pushNotificationType: PushNotificationType, locales: Seq[Locale], args : Any*): String

}
