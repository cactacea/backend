package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.PushNotificationType
import io.github.cactacea.backend.core.infrastructure.identifiers._

case class PushNotification(
                             displayName: String,
                             pushNotificationType: PushNotificationType,
                             postedAt: Long,
                             tokens: List[(AccountId, String)],
                             sessionId: SessionId,
                             message: Option[String],
                             url: String
                           )

object PushNotification {

  def apply(displayName: String, pushNotificationType: PushNotificationType, postedAt: Long, tokens: List[(AccountId, String)], sessionId: SessionId, url: String): PushNotification = {
    PushNotification(
      displayName,
      pushNotificationType,
      postedAt,
      tokens,
      sessionId,
      None,
      url)
  }

}

