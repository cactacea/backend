package io.github.cactacea.core.application.components.services

import com.google.inject.Singleton
import com.osinka.i18n.{Lang, Messages}
import io.github.cactacea.core.application.components.interfaces.NotificationMessagesService
import io.github.cactacea.core.domain.enums.PushNotificationType

@Singleton
class DefaultNotificationMessagesService extends NotificationMessagesService {

  def get(pushNotificationType: PushNotificationType, lang: Lang, args : Any*): String = {
    val message = pushNotificationType match {
      case PushNotificationType.`message` => "send_message"
      case PushNotificationType.`noDisplayedMessage` => "send_no_displayed_message"
      case PushNotificationType.`image` => "send_image"
      case PushNotificationType.`groupInvitation` => "send_group_invitation"
      case PushNotificationType.`friendRequest` => "send_friend_request"
      case PushNotificationType.`feed` => "post_feed"
      case PushNotificationType.`comment` => "post_comment"
    }
    Messages(message)(lang).format(args)
  }

}

