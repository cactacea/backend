package io.github.cactacea.backend.core.application.components.services

import java.util.Locale

import com.osinka.i18n.{Lang, Messages}
import io.github.cactacea.backend.core.application.components.interfaces.MessageService
import io.github.cactacea.backend.core.domain.enums.{NotificationType, PushNotificationType}

class DefaultMessageService extends MessageService {

  private def validLanguage(locales: Seq[Locale]): Lang = {
    val language = locales.filter(l => l == Locale.US || l == Locale.JAPAN).headOption.getOrElse(Locale.US)
    Lang(language)
  }

  def getPushNotificationMessage(pushNotificationType: PushNotificationType, locales: Seq[Locale], args : Any*): String = {
    val message = pushNotificationType match {
      case PushNotificationType.message => "message"
      case PushNotificationType.`nonDisplay` => "message_no_displayed"
      case PushNotificationType.image => "message_image"
      case PushNotificationType.`invitation` => "channel_invitation"
      case PushNotificationType.`request` => "friend_request"
      case PushNotificationType.tweet => "tweet"
      case PushNotificationType.tweetReply => "tweet_reply"
      case PushNotificationType.commentReply => "comment_reply"
    }
    val lang = validLanguage(locales)
    Messages(message, args:_*)(lang)
  }

  def getNotificationMessage(notificationType: NotificationType, locales: Seq[Locale], args : Any*): String = {
    val message = notificationType match {
      case NotificationType.operator => "operator"
      case NotificationType.`invitation` => "channel_invitation"
      case NotificationType.`friendRequest` => "friend_request"
      case NotificationType.tweet => "tweet"
      case NotificationType.tweetReply => "tweet_reply"
      case NotificationType.commentReply => "comment_reply"
    }
    val lang = validLanguage(locales)
    Messages(message, args:_*)(lang)
  }

}

