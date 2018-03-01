package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.models.PushNotificationSettings

case class PushNotificationSetting(
                                    followerFeed: Boolean,
                                    feedComment: Boolean,
                                    groupMessage: Boolean,
                                    directMessage: Boolean,
                                    groupInvite: Boolean,
                                    showMessage: Boolean
                               )

object PushNotificationSetting {

  def apply(s: PushNotificationSettings): PushNotificationSetting = {
    PushNotificationSetting(
      groupInvite              = s.groupInvite,
      followerFeed        = s.followerFeed,
      feedComment         = s.feedComment,
      groupMessage        = s.groupMessage,
      directMessage       = s.directMessage,
      showMessage         = s.showMessage
    )
  }

}