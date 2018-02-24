package io.github.cactacea.core.application.requests.setting

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.FormParam
import io.github.cactacea.core.domain.models.PushNotificationSetting

case class PutNotificationSetting(
                                   @FormParam notificationSetting: PushNotificationSetting,
                                   session: Request
                                 )