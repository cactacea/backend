package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.PushNotificationSetting
import io.github.cactacea.core.infrastructure.models.PushNotificationSettings

object PushNotificationSettingFactory {

  def create(s: PushNotificationSettings): PushNotificationSetting = {
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
