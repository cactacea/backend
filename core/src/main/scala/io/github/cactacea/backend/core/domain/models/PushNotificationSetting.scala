package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.models.PushNotificationSettings

case class PushNotificationSetting(
                                    tweet: Boolean,
                                    comment: Boolean,
                                    friendRequest: Boolean,
                                    message: Boolean,
                                    channelMessage: Boolean,
                                    invitation: Boolean,
                                    showMessage: Boolean
                               )

object PushNotificationSetting {

  def apply(s: PushNotificationSettings): PushNotificationSetting = {
    PushNotificationSetting(
      tweet            = s.tweet,
      comment         = s.comment,
      friendRequest   = s.friendRequest,
      message         = s.message,
      channelMessage    = s.channelMessage,
      invitation = s.invitation,
      showMessage     = s.showMessage
    )
  }

}
