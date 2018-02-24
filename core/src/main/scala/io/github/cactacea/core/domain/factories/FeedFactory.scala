package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.{Feed, Account}
import io.github.cactacea.core.infrastructure.models._

object FeedFactory {

  def create(f: Feeds): Feed = {
    Feed(
      id              = f.id,
      message         = f.message,
      mediums          = None,
      tags            = None,
      account         = None,
      favoriteCount   = f.favoriteCount,
      commentCount    = f.commentCount,
      contentWarning  = f.contentWarning,
      postedAt        = f.postedAt
    )
  }

  def create(f: Feeds, ft: List[FeedTags], m: List[Mediums]): Feed = {
    val tags = ft.map(_.name)
    val images = m.map(MediumFactory.create(_))
    Feed(
      id              = f.id,
      message         = f.message,
      mediums          = Some(images),
      tags            = Some(tags),
      account         = None,
      favoriteCount   = f.favoriteCount,
      commentCount    = f.commentCount,
      contentWarning  = f.contentWarning,
      postedAt        = f.postedAt
    )
  }

  def create(f: Feeds, ft: List[FeedTags], i: List[Mediums], u: Accounts, r: Option[Relationships]): Feed = {
    val account: Option[Account] = Some(AccountFactory.create(u, r))
    val images = i.map(MediumFactory.create(_))
    val tags = ft.map(_.name)
    Feed(
      id              = f.id,
      message         = f.message,
      mediums          = Some(images),
      tags            = Some(tags),
      account         = account,
      favoriteCount   = f.favoriteCount,
      commentCount    = f.commentCount,
      contentWarning  = f.contentWarning,
      postedAt        = f.postedAt
    )
  }

}
