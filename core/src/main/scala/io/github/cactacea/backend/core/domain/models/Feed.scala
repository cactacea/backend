package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{UserStatusType, ContentStatusType}
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.github.cactacea.backend.core.infrastructure.models.{Feeds, _}

case class Feed(
                 id: FeedId,
                 message: String,
                 mediums: List[Medium],
                 tags: Option[List[String]],
                 user: Option[User],
                 likeCount: Long,
                 commentCount: Long,
                 liked: Boolean,
                 warning: Boolean,
                 rejected: Boolean,
                 postedAt: Long,
                 next: Long
                 )

object Feed {

  def apply(f: Feeds, l: Option[FeedLikes], m: List[Mediums], a: Users, r: Option[Relationships], next: Long): Feed = {
    val rejected = (f.contentStatus == ContentStatusType.rejected) || (a.userStatus != UserStatusType.normally)
    rejected match {
      case true => {
        Feed(
          id              = f.id,
          message         = "",
          mediums         = List[Medium](),
          tags            = None,
          user         = None,
          likeCount       = 0L,
          commentCount    = 0L,
          liked           = false,
          warning         = false,
          rejected        = rejected,
          postedAt        = f.postedAt,
          next            = next
        )
      }
      case false => {
        val images = m.map(Medium(_))
        Feed(
          id              = f.id,
          message         = f.message,
          mediums         = images,
          tags            = f.tags.map(_.split(' ').toList),
          user         = Option(User(a, r)),
          likeCount       = f.likeCount,
          commentCount    = f.commentCount,
          liked           = l.isDefined,
          warning         = f.contentWarning,
          rejected        = rejected,
          postedAt        = f.postedAt,
          next            = next
        )
      }
    }
  }


}


