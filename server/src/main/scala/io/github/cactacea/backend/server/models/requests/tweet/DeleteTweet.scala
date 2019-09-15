package io.github.cactacea.backend.server.models.requests.tweet

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.TweetId
import io.swagger.annotations.ApiModelProperty

case class DeleteTweet(
                       @ApiModelProperty(value = "Tweet identifier.", required = true)
                       @RouteParam id: TweetId
                  )
