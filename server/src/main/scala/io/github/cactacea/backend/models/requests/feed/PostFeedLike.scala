package io.github.cactacea.backend.models.requests.feed

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.swagger.annotations.ApiModelProperty

case class PostFeedLike(
                         @ApiModelProperty(value = "Feed identifier.", required = true)
                         @RouteParam id: FeedId
                       )
