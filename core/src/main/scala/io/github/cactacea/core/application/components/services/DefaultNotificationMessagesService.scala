package io.github.cactacea.core.application.components.services

import java.util.Locale

import com.osinka.i18n.{Lang, Messages}
import io.github.cactacea.core.application.components.interfaces.NotificationMessagesService
import io.github.cactacea.core.domain.enums.PushNotificationType

class DefaultNotificationMessagesService extends NotificationMessagesService {

  private def validLanguage(locales: Seq[Locale]): Lang = {
    val language = locales.filter(l => l == Locale.US || l == Locale.JAPAN).headOption.getOrElse(Locale.US)
    Lang(language)
  }

  def getPushNotification(pushNotificationType: PushNotificationType, locales: Seq[Locale], args : Any*): String = {
    val message = pushNotificationType match {
      case PushNotificationType.`message` => "send_message"
      case PushNotificationType.`noDisplayedMessage` => "send_no_displayed_message"
      case PushNotificationType.`image` => "send_image"
      case PushNotificationType.`groupInvitation` => "send_group_invitation"
      case PushNotificationType.`friendRequest` => "send_friend_request"
      case PushNotificationType.`feed` => "post_feed"
      case PushNotificationType.`comment` => "post_comment"
    }
    val lang = validLanguage(locales)
    Messages(message)(lang).format(args)
  }

}

