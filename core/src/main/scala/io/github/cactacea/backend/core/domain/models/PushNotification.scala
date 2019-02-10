package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.PushNotificationType

case class PushNotification(
                             displayName: String,
                             message: Option[String],
                             postedAt: Long,
                             url: String,
                             destinations: List[Destination],
                             notificationType: PushNotificationType
                           )
