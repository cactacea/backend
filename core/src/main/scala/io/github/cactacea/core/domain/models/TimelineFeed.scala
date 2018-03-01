package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.TimelineFeedId
import io.github.cactacea.core.infrastructure.models._

case class TimelineFeed (
                          id: TimelineFeedId,
                          feed: Option[Feed],
                          postedAt: Long
                    )

object TimelineFeed {

  def apply(tm: Timelines, tt: Option[Feeds], ttg: Option[List[FeedTags]], i: Option[List[Mediums]], u: Option[Accounts], r: Option[Relationships]): TimelineFeed = {
    val feed = (tt, ttg, i, u, r) match {
      case (Some(tt), Some(ttg), Some(i), Some(u), us) =>
        Some(Feed(tt, ttg, i, u, us))
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