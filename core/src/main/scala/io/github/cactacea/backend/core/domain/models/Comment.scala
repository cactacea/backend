package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{UserStatusType, ContentStatusType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, TweetId}
import io.github.cactacea.backend.core.infrastructure.models.{Users, Comments, Relationships}

case class Comment(
                    id: CommentId,
                    replyId: Option[CommentId],
                    tweetId: TweetId,
                    message: String,
                    user: User,
                    likeCount: Long,
                    warning: Boolean,
                    rejected: Boolean,
                    postedAt: Long,
                    next: Long)

object Comment {

  def apply(c: Comments, a: Users, r: Option[Relationships]): Comment = {
    val rejected = (c.contentStatus == ContentStatusType.rejected) || (a.userStatus != UserStatusType.normally)
    rejected match {
      case true =>
        Comment(
          id              = c.id,
          replyId         = c.replyId,
          tweetId          = c.tweetId,
          message         = "",
          user         = User(a, r),
          likeCount       = 0L,
          warning         = false,
          rejected        = rejected,
          postedAt        = c.postedAt,
          next            = c.id.value
        )
      case false => {
        Comment(
          id              = c.id,
          replyId         = c.replyId,
          tweetId          = c.tweetId,
          message         = c.message,
          user         = User(a, r),
          likeCount       = c.likeCount,
          warning         = c.contentWarning,
          rejected        = rejected,
          postedAt        = c.postedAt,
          next            = c.id.value
        )
      }
    }
  }

}
