package io.github.cactacea.backend.models.requests.feed

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class GetFeed(
                    @RouteParam id: FeedId
                       )
