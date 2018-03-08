package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.ContentStatusType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, CommentId, FeedId}

case class Comments(
                     id: CommentId,
                     message: String,
                     feedId: FeedId,
                     replyId: Option[CommentId],
                     favoriteCount: Long,
                     by: AccountId,
                     contentWarning: Boolean,
                     contentStatus: ContentStatusType,
                     notified: Boolean,
                     postedAt: Long
                  )
