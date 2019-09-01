package io.github.cactacea.backend.server.models.requests.channel

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation._
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.swagger.annotations.ApiModelProperty


case class GetUserChannels(
                               @ApiModelProperty(value = "User identifier.", required = true)
                             @RouteParam id: UserId,

                               @ApiModelProperty(value = "Filters channels which started on since or later.")
                             @QueryParam since: Option[Long],

                               @ApiModelProperty(value = "The offset of channels. By default the value is 0.")
                             @QueryParam offset: Option[Int],

                               @ApiModelProperty(value = "Maximum number of channels returned on one result page." +
                               " By default the value is 20 channels. The page size can never be larger than 50.")
                             @QueryParam @Max(50) count: Option[Int]
                      )
