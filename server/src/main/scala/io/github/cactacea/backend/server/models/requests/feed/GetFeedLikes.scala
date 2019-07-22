package io.github.cactacea.backend.server.models.requests.feed

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation.Max
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.swagger.annotations.ApiModelProperty

case class GetFeedLikes(
                         @ApiModelProperty(value = "Feed identifier.", required = true)
                         @RouteParam id: FeedId,

                         @ApiModelProperty(value = "Filters accounts which started on since or later.")
                         @QueryParam since: Option[Long],

                         @ApiModelProperty(value = "The offset of accounts. By default the value is 0.")
                         @QueryParam offset: Option[Int],

                         @ApiModelProperty(value = "Maximum number of accounts returned on one result page." +
                           " By default the value is 20 entries. The page size can never be larger than 50.")
                         @QueryParam @Max(50) count: Option[Int]
                    )
