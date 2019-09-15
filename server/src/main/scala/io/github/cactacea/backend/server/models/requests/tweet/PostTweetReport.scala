package io.github.cactacea.backend.server.models.requests.tweet

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.infrastructure.identifiers.TweetId
import io.swagger.annotations.ApiModelProperty

case class PostTweetReport(
                           @ApiModelProperty(value = "Tweet identifier.", required = true)
                           @RouteParam id: TweetId,

                           @ApiModelProperty(value = "Report type.", required = true)
                           reportType: ReportType,

                           @ApiModelProperty(value = "Description about this report.")
                           reportContent: Option[String]
                         )
