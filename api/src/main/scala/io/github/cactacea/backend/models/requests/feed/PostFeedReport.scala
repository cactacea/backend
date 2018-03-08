package io.github.cactacea.backend.models.requests.feed

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{FormParam, RouteParam}
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class PostFeedReport(
                           @RouteParam feedId: FeedId,
                           reportType: ReportType,
                           session: Request
                     )
