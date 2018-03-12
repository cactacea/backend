package io.github.cactacea.core.domain.models

import com.google.inject.Inject
import io.github.cactacea.core.application.components.interfaces.NotificationMessagesService
import io.github.cactacea.core.domain.enums.ContentStatusType
import io.github.cactacea.core.infrastructure.identifiers.FeedId
import io.github.cactacea.core.infrastructure.models._

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

  @Inject private var notificationMessagesService: NotificationMessagesService = _

  def apply(f: Feeds): Feed = {
    _apply(f, None, None, None, None, f.id.value)
  }

  def apply(f: Feeds, next: Long): Feed = {
    _apply(f, None, None, None, None, next)
  }

  def apply(f: Feeds, ft: List[FeedTags], m: List[Mediums]): Feed = {
    _apply(f, Some(ft), Some(m), None, None, f.id.value)
  }

  def apply(f: Feeds, ft: List[FeedTags], m: List[Mediums], a: Accounts, r: Option[Relationships]): Feed = {
    _apply(f, Some(ft), Some(m), Some(a), r, f.id.value)
  }

  private def _apply(f: Feeds, ft: Option[List[FeedTags]], m: Option[List[Mediums]], a: Option[Accounts], r: Option[Relationships], next: Long): Feed = {
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
          next            = next
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
          next            = next
        )
      }
    }
  }


}


