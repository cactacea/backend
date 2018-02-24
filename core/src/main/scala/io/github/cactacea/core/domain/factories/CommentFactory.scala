package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.{Comment, Feed}
import io.github.cactacea.core.infrastructure.models.{Accounts, Comments, Relationships}

object CommentFactory {

  def create(c: Comments, a: Accounts, r: Option[Relationships]): Comment = {
    val account = AccountFactory.create(a, r)
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
