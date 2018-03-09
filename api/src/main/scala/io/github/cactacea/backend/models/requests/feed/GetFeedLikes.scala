package io.github.cactacea.backend.models.requests.feed

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation.Max
import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class GetFeedLikes(
                             @RouteParam feedId: FeedId,
                             @QueryParam since: Option[Long],
                             @QueryParam offset: Option[Int],
                             @QueryParam @Max(50) count: Option[Int]
                    )
