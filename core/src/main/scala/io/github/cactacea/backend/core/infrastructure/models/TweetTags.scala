package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{TweetId}

case class TweetTags(
                      tweetId: TweetId,
                      name: String,
                      orderNo: Long
                   )
