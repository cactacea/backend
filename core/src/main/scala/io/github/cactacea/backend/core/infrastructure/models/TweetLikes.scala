package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, TweetId, TweetLikeId}

case class TweetLikes(
                       id: TweetLikeId,
                       tweetId: TweetId,
                       by: UserId,
                       likedAt: Long
                    )
