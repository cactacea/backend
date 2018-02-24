package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{FeedId, AccountId}

case class FeedFavorites(
                          feedId: FeedId,
                          by: AccountId,
                          postedAt: Long
                         )
