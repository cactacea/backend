package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{UserStatusType, ContentStatusType}
import io.github.cactacea.backend.core.infrastructure.identifiers.TweetId
import io.github.cactacea.backend.core.infrastructure.models.{Tweets, _}

case class Tweet(
                 id: TweetId,
                 message: String,
                 mediums: Seq[Medium],
                 tags: Option[Seq[String]],
                 user: Option[User],
                 likeCount: Long,
                 commentCount: Long,
                 liked: Boolean,
                 warning: Boolean,
                 rejected: Boolean,
                 postedAt: Long,
                 next: Long
                 )

object Tweet {

  def apply(f: Tweets, l: Option[TweetLikes], m: Seq[Mediums], a: Users, r: Option[Relationships], next: Long): Tweet = {
    val rejected = (f.contentStatus == ContentStatusType.rejected) || (a.userStatus != UserStatusType.normally)
    rejected match {
      case true => {
        Tweet(
          id              = f.id,
          message         = "",
          mediums         = Seq[Medium](),
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
        Tweet(
          id              = f.id,
          message         = f.message,
          mediums         = images,
          tags            = f.tags.map(_.split(' ').toSeq),
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


