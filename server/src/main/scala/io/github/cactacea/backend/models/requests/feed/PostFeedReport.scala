package io.github.cactacea.backend.models.requests.feed

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.swagger.annotations.ApiModelProperty

case class PostFeedReport(
                           @ApiModelProperty(value = "Feed identifier.", required = true)
                           @RouteParam id: FeedId,

                           @ApiModelProperty(value = "Report type.", required = true)
                           reportType: ReportType,

                           @ApiModelProperty(value = "Description about this report.")
                           reportContent: Option[String]
                         )
