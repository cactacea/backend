package io.github.cactacea.backend.core.application.components.interfaces

import java.util.Locale

import io.github.cactacea.backend.core.domain.enums.{FeedType, PushNotificationType}

trait MessageService {

  def getPushNotificationMessage(pushNotificationType: PushNotificationType, locales: Seq[Locale], args : Any*): String
  def getPushMessage(feedType: FeedType, locales: Seq[Locale], args : Any*): String

}
