package io.github.cactacea.backend.core.application.components.services

import java.util.Locale

import com.osinka.i18n.{Lang, Messages}
import io.github.cactacea.backend.core.application.components.interfaces.MessageService
import io.github.cactacea.backend.core.domain.enums.{InformationType, NotificationType}

class DefaultMessageService extends MessageService {

  private def validLanguage(locales: Seq[Locale]): Lang = {
    val language = locales.filter(l => l == Locale.US || l == Locale.JAPAN).headOption.getOrElse(Locale.US)
    Lang(language)
  }

  def getNotificationMessage(notificationType: NotificationType, locales: Seq[Locale], args : Any*): String = {
    val message = notificationType match {
      case NotificationType.message => "message"
      case NotificationType.`nonDisplay` => "message_no_displayed"
      case NotificationType.image => "message_image"
      case NotificationType.`invitation` => "channel_invitation"
      case NotificationType.`request` => "friend_request"
      case NotificationType.tweet => "tweet"
      case NotificationType.tweetReply => "tweet_reply"
      case NotificationType.commentReply => "comment_reply"
    }
    val lang = validLanguage(locales)
    Messages(message, args:_*)(lang)
  }

  def getPushMessage(informationType: InformationType, locales: Seq[Locale], args : Any*): String = {
    val message = informationType match {
      case InformationType.operator => "operator"
      case InformationType.`invitation` => "channel_invitation"
      case InformationType.`friendRequest` => "friend_request"
      case InformationType.tweet => "tweet"
      case InformationType.tweetReply => "tweet_reply"
      case InformationType.commentReply => "comment_reply"
    }
    val lang = validLanguage(locales)
    Messages(message, args:_*)(lang)
  }

}

