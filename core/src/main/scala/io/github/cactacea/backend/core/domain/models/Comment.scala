package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, ContentStatusType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{CommentId, FeedId}
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Comments, Relationships}

case class Comment(
                    id: CommentId,
                    replyId: Option[CommentId],
                    feedId: FeedId,
                    message: String,
                    account: Account,
                    likeCount: Long,
                    warning: Boolean,
                    rejected: Boolean,
                    postedAt: Long,
                    next: Long)

object Comment {

  def apply(c: Comments, a: Accounts, r: Option[Relationships]): Comment = {
    val rejected = (c.contentStatus == ContentStatusType.rejected) || (a.accountStatus != AccountStatusType.normally)
    rejected match {
      case true =>
        Comment(
          id              = c.id,
          replyId         = c.replyId,
          feedId          = c.feedId,
          message         = "",
          account         = Account(a, r),
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
          feedId          = c.feedId,
          message         = c.message,
          account         = Account(a, r),
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
