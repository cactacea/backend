package io.github.cactacea.core.domain.models

import io.github.cactacea.core.domain.enums.PushNotificationType
import io.github.cactacea.core.infrastructure.identifiers._

case class PushNotification(
                           displayName: String,
                           pushNotificationType: PushNotificationType,
                           postedAt: Long,
                           tokens: List[(AccountId, String)],
                           sessionId: SessionId,
                           message: Option[String],
                           urlIos: Option[String],
                           urlAndroid: Option[String]
                           )
