package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ContentStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, CommentId, FeedId}

case class Comments(
                     id: CommentId,
                     message: String,
                     feedId: FeedId,
                     replyId: Option[CommentId],
                     likeCount: Long,
                     by: AccountId,
                     contentWarning: Boolean,
                     contentStatus: ContentStatusType,
                     notified: Boolean,
                     postedAt: Long
                  )
