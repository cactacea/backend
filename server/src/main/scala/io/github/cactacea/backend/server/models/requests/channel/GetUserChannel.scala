package io.github.cactacea.backend.server.models.requests.channel

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.swagger.annotations.ApiModelProperty

case class GetUserChannel(
                            @ApiModelProperty(value = "User identifier.", required = true)
                            @RouteParam id: UserId
                    )
