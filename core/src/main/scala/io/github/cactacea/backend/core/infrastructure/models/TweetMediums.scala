package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, TweetId}

case class TweetMediums(
                         tweetId: TweetId,
                         mediumId: MediumId,
                         orderNo: Long
                       )
