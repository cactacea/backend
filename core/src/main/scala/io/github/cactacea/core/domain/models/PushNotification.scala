package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers._

case class PushNotification(
                   message: String,
                   urlIos: String,
                   urlAndroid: String,
                   postedAt: Long,
                   tokens: List[(AccountId, String)],
                   sessionId: SessionId
                       )
