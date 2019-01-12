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
      case PushNotificationType.noDisplayedMessage => "no_displayed_message"
      case PushNotificationType.image => "image"
      case PushNotificationType.groupInvitation => "group_invitation"
      case PushNotificationType.friendRequest => "friend_request"
      case PushNotificationType.feed => "feed"
      case PushNotificationType.feedReply => "feed_reply"
      case PushNotificationType.commentReply => "comment_reply"
    }
    val lang = validLanguage(locales)
    Messages(message)(lang).format(args)
  }

  def getNotificationMessage(notificationType: NotificationType, locales: Seq[Locale], args : Any*): String = {
    val message = notificationType match {
      case NotificationType.operator => "operator"
      case NotificationType.groupInvitation => "group_invitation"
      case NotificationType.friendRequest => "friend_request"
      case NotificationType.feed => "feed"
      case NotificationType.feedReply => "feed_reply"
      case NotificationType.commentReply => "comment_reply"
    }
    val lang = validLanguage(locales)
    Messages(message)(lang).format(args)
  }

}

