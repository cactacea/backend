package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{CommentId, FeedId, AccountId}

case class Comments(
                     id: CommentId,
                     message: String,
                     feedId: FeedId,
                     favoriteCount: Long,
                     by: AccountId,
                     notified: Boolean,
                     postedAt: Long
                  )
