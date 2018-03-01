package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.FeedId
import io.github.cactacea.core.infrastructure.models._

case class Feed(
                 id: FeedId,
                 message: String,
                 mediums: Option[List[Medium]],
                 tags: Option[List[String]],
                 account: Option[Account],
                 favoriteCount: Long,
                 commentCount: Long,
                 contentWarning: Boolean,
                 postedAt: Long
                 )

object Feed {

  def apply(f: Feeds): Feed = {
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

  def apply(f: Feeds, ft: List[FeedTags], m: List[Mediums]): Feed = {
    val tags = ft.map(_.name)
    val images = m.map(Medium(_))
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

  def apply(f: Feeds, ft: List[FeedTags], i: List[Mediums], u: Accounts, r: Option[Relationships]): Feed = {
    val account: Option[Account] = Some(Account(u, r))
    val images = i.map(Medium(_))
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