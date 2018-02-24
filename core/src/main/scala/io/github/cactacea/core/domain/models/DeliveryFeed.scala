package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId}

case class DeliveryFeed(
                         accountId: AccountId,
                         displayName: String,
                         feedId: FeedId,
                         message: String,
                         postedAt: Long,
                         tokens: List[(String, AccountId)]
               )
