package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, FeedLikeId}

case class FeedLikes(
                    id: FeedLikeId,
                    feedId: FeedId,
                    by: AccountId,
                    postedAt: Long
                    )
