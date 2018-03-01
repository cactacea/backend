package io.github.cactacea.backend.models.requests.setting

import com.twitter.finagle.http.Request

case class GetPushNotificationSetting(
                                       session: Request
                                 )