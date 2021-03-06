package io.github.cactacea.backend.core.application.components.interfaces

import java.util.Locale

import io.github.cactacea.backend.core.domain.enums.{InformationType, NotificationType}

trait MessageService {

  def getNotificationMessage(notificationType: NotificationType, locales: Seq[Locale], args : Any*): String
  def getPushMessage(feedType: InformationType, locales: Seq[Locale], args : Any*): String

}
