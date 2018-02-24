package io.github.cactacea.core.application.requests.feed

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class GetFeed(
                    @RouteParam feedId: FeedId,
                    session: Request
                       )
