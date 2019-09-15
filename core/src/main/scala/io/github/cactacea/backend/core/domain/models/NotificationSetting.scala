package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.models.NotificationSettings

case class NotificationSetting(
                                    tweet: Boolean,
                                    comment: Boolean,
                                    friendRequest: Boolean,
                                    message: Boolean,
                                    channelMessage: Boolean,
                                    invitation: Boolean,
                                    showMessage: Boolean
                               )

object NotificationSetting {

  def apply(s: NotificationSettings): NotificationSetting = {
    NotificationSetting(
      tweet           = s.tweet,
      comment         = s.comment,
      friendRequest   = s.friendRequest,
      message         = s.message,
      channelMessage  = s.channelMessage,
      invitation      = s.invitation,
      showMessage     = s.showMessage
    )
  }

}
