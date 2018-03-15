package io.github.cactacea.backend.models.requests.feed

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers.FeedId
import io.swagger.annotations.ApiModelProperty

case class PostFeedReport(
                           @ApiModelProperty(value = "Feed identifier.")
                           @RouteParam id: FeedId,
                           reportType: ReportType
                     )
