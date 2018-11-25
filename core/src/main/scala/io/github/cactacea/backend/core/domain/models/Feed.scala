package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.ContentStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.github.cactacea.backend.core.infrastructure.models.{Feeds, _}

case class Feed(
                 id: FeedId,
                 message: String,
                 mediums: Option[List[Medium]],
                 tags: Option[List[String]],
                 account: Option[Account],
                 likeCount: Long,
                 commentCount: Long,
                 contentWarning: Boolean,
                 contentDeleted: Boolean,
                 postedAt: Long,
                 next: Long
                 )

object Feed {

  def apply(f: Feeds): Feed = {
    apply(f, None, None, None, None, None)
  }

  def apply(f: Feeds, fl: FeedLikes): Feed = {
    apply(f, Some(fl), None, None, None, None)
  }

  def apply(f: Feeds, ft: List[FeedTags], m: List[Mediums]): Feed = {
    apply(f, None, Some(ft), Some(m), None, None)
  }

  def apply(f: Feeds, ft: List[FeedTags], m: List[Mediums], a: Accounts, r: Option[Relationships]): Feed = {
    apply(f, None, Some(ft), Some(m), Some(a), r)
  }

  private def apply(f: Feeds, fl: Option[FeedLikes], ft: Option[List[FeedTags]], m: Option[List[Mediums]], a: Option[Accounts], r: Option[Relationships]): Feed = {
    f.contentStatus match {
      case ContentStatusType.rejected => {
        Feed(
          id              = f.id,
          message         = "",
          mediums         = None,
          tags            = None,
          account         = None,
          likeCount       = 0L,
          commentCount    = 0L,
          contentWarning  = false,
          contentDeleted  = true,
          postedAt        = f.postedAt,
          next            = fl.map(_.postedAt).getOrElse(f.postedAt)
        )
      }
      case _ => {
        val account: Option[Account] = a.map(Account(_, r))
        val images = m.map(_.map(Medium(_)))
        val tags = ft.map(_.map(_.name))
        Feed(
          id              = f.id,
          message         = f.message,
          mediums         = images,
          tags            = tags,
          account         = account,
          likeCount       = f.likeCount,
          commentCount    = f.commentCount,
          contentWarning  = f.contentWarning,
          contentDeleted  = false,
          postedAt        = f.postedAt,
          next            = fl.map(_.postedAt).getOrElse(f.postedAt)
        )
      }
    }
  }


}


