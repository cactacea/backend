package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class Feed(
                 id: FeedId,
                 message: String,
                 mediums: Option[List[Medium]],
                 tags: Option[List[String]],
                 account: Option[Account],
                 favoriteCount: Long,
                 commentCount: Long,
                 contentWarning: Boolean,
                 postedAt: Long
                 )
