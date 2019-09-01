package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, FeedId, FeedLikeId}

case class FeedLikes(
                      id: FeedLikeId,
                      feedId: FeedId,
                      by: UserId,
                      likedAt: Long
                    )
