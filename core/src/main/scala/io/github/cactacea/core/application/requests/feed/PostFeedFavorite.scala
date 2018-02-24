package io.github.cactacea.core.application.requests.feed

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class PostFeedFavorite(
                             @RouteParam feedId: FeedId,
                             session: Request
  )