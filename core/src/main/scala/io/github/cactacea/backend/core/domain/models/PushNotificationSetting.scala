package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.models.PushNotificationSettings

case class PushNotificationSetting(
                                    feed: Boolean,
                                    comment: Boolean,
                                    friendRequest: Boolean,
                                    message: Boolean,
                                    groupMessage: Boolean,
                                    groupInvitation: Boolean,
                                    showMessage: Boolean
                               )

object PushNotificationSetting {

  def apply(s: PushNotificationSettings): PushNotificationSetting = {
    PushNotificationSetting(
      feed            = s.feed,
      comment         = s.comment,
      friendRequest   = s.friendRequest,
      message         = s.message,
      groupMessage    = s.groupMessage,
      groupInvitation = s.groupInvitation,
      showMessage     = s.showMessage
    )
  }

}
