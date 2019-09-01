package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, FeedId}

case class UserFeeds(
                          userId: UserId,
                          feedId: FeedId,
                          by: UserId,
                          notified: Boolean,
                          postedAt: Long
                        )
