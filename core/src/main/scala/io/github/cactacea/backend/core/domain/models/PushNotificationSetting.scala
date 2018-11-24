package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.models.PushNotificationSettings

case class PushNotificationSetting(
                                    followerFeed: Boolean,
                                    feedComment: Boolean,
                                    groupMessage: Boolean,
                                    directMessage: Boolean,
                                    groupInvitation: Boolean,
                                    showMessage: Boolean
                               )

object PushNotificationSetting {

  def apply(s: PushNotificationSettings): PushNotificationSetting = {
    PushNotificationSetting(
      groupInvitation              = s.groupInvitation,
      followerFeed        = s.followerFeed,
      feedComment         = s.feedComment,
      groupMessage        = s.groupMessage,
      directMessage       = s.directMessage,
      showMessage         = s.showMessage
    )
  }

}
