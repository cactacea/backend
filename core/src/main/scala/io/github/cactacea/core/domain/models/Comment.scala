package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.CommentId
import io.github.cactacea.core.infrastructure.models.{Accounts, Comments, Relationships}

case class Comment(
                    id: CommentId,
                    message: String,
                    account: Account,
                    feed: Option[Feed],
                    favoriteCount: Long,
                    postedAt: Long)

object Comment {

  def apply(c: Comments, a: Accounts, r: Option[Relationships]): Comment = {
    val account = Account(a, r)
    val feed: Option[Feed] = None
    Comment(
      id            = c.id,
      message       = c.message,
      account       = account,
      feed          = feed,
      favoriteCount = c.favoriteCount,
      postedAt      = c.postedAt
    )
  }

}