package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.TimelineFeedId
import io.github.cactacea.core.infrastructure.models._

case class TimelineFeed (
                          id: TimelineFeedId,
                          feed: Option[Feed],
                          postedAt: Long
                    )

object TimelineFeed {

  def apply(tm: Timelines, tt: Option[Feeds], ttg: Option[List[FeedTags]], i: Option[List[Mediums]], a: Option[Accounts], r: Option[Relationships]): TimelineFeed = {
    val feed = (tt, ttg, i, a, r) match {
      case (Some(tt), Some(ttg), Some(i), Some(a), us) =>
        Some(Feed(tt, ttg, i, a, us))
      case _ =>
        None
    }
    TimelineFeed(
      id        = tm.id,
      feed      = feed,
      postedAt  = tm.postedAt
    )
  }

}
