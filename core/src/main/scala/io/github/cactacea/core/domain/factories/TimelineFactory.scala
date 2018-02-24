package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.TimelineFeed
import io.github.cactacea.core.infrastructure.models._

object TimelineFactory {

  def create(tm: Timelines, tt: Option[Feeds], ttg: Option[List[FeedTags]], i: Option[List[Mediums]], u: Option[Accounts], r: Option[Relationships]): TimelineFeed = {
    val feed = (tt, ttg, i, u, r) match {
      case (Some(tt), Some(ttg), Some(i), Some(u), us) =>
        Some(FeedFactory.create(tt, ttg, i, u, us))
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
