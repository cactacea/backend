package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.NotificationType

case class Notification(
                             displayName: String,
                             message: Option[String],
                             postedAt: Long,
                             url: String,
                             destinations: Seq[Destination],
                             feedType: NotificationType
                           )
