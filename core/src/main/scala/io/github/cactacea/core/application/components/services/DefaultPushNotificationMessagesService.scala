package io.github.cactacea.core.application.components.services

import com.osinka.i18n.{Lang, Messages}
import io.github.cactacea.core.application.components.interfaces.PushNotificationMessagesService
import io.github.cactacea.core.domain.enums.PushNotificationType

class DefaultPushNotificationMessagesService extends PushNotificationMessagesService {

  def get(pushNotificationType: PushNotificationType, lang: Lang, args : Any*): String = {
    val message = pushNotificationType match {
      case PushNotificationType.sendMessage => "send_message"
      case PushNotificationType.sendNoDisplayedMessage => "send_no_displayed_message"
      case PushNotificationType.sendImage => "send_image"
      case PushNotificationType.sendGroupInvitation => "send_group_invitation"
      case PushNotificationType.sendFriendRequest => "send_friend_request"
      case PushNotificationType.postFeed => "post_feed"
      case PushNotificationType.postComment => "post_comment"
    }
    Messages(message)(lang).format(args)
  }

}

