package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.FeedType
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.github.cactacea.backend.core.infrastructure.models.Feeds

case class Feed(
                 id: FeedId,
                 feedType: FeedType,
                 contentId: Option[Long],
                 message: String,
                 url: String,
                 notifiedAt: Long,
                 next: Long
                         )

object Feed {

  def apply(n: Feeds, message: String, nextId: Long): Feed = {
    new Feed(
      n.id,
      n.feedType,
      n.contentId,
      message,
      n.url,
      n.notifiedAt,
      nextId)
  }

}
