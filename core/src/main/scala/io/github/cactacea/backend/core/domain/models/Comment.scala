package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.ContentStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.github.cactacea.backend.core.infrastructure.models.{Accounts, Comments, Relationships}

case class Comment(
                    id: CommentId,
                    replyId: Option[CommentId],
                    message: String,
                    account: Account,
                    likeCount: Long,
                    contentWarning: Boolean,
                    contentDeleted: Boolean,
                    postedAt: Long,
                    next: Long)

object Comment {

  def apply(c: Comments, a: Accounts, r: Option[Relationships]): Comment = {
    val account = Account(a, r)
    c.contentStatus match {
      case ContentStatusType.rejected =>
        Comment(
          id              = c.id,
          replyId         = c.replyId,
          message         = "",
          account         = account,
          likeCount       = 0L,
          contentWarning  = false,
          contentDeleted  = true,
          postedAt        = c.postedAt,
          next            = c.postedAt
        )
      case _ => {
        Comment(
          id              = c.id,
          replyId         = c.replyId,
          message         = c.message,
          account         = account,
          likeCount       = c.likeCount,
          contentWarning  = c.contentWarning,
          contentDeleted  = false,
          postedAt        = c.postedAt,
          next            = c.postedAt
        )
      }
    }
  }

}
