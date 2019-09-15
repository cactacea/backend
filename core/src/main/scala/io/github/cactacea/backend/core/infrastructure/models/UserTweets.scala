package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, TweetId}

case class UserTweets(
                      userId: UserId,
                      tweetId: TweetId,
                      by: UserId,
                      notified: Boolean,
                      postedAt: Long
                        )
