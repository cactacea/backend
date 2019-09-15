package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ContentStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, CommentId, TweetId}

case class Comments(
                     id: CommentId,
                     message: String,
                     tweetId: TweetId,
                     replyId: Option[CommentId],
                     likeCount: Long,
                     by: UserId,
                     contentWarning: Boolean,
                     contentStatus: ContentStatusType,
                     notified: Boolean,
                     postedAt: Long
                  )
