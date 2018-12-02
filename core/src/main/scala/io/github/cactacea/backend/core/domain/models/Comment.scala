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
                    next: Option[Long])

object Comment {

  def apply(c: Comments, a: Accounts, r: Option[Relationships]): Comment = {
    c.contentStatus match {
      case ContentStatusType.rejected =>
        Comment(
          id              = c.id,
          replyId         = c.replyId,
          message         = "",
          account         = Account(a, r),
          likeCount       = 0L,
          contentWarning  = false,
          contentDeleted  = true,
          postedAt        = c.postedAt,
          next            = None
        )
      case _ => {
        Comment(
          id              = c.id,
          replyId         = c.replyId,
          message         = c.message,
          account         = Account(a, r),
          likeCount       = c.likeCount,
          contentWarning  = c.contentWarning,
          contentDeleted  = false,
          postedAt        = c.postedAt,
          next            = Some(c.id.value)
        )
      }
    }
  }

}
