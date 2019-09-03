package io.github.cactacea.backend.server.models.requests.channel

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId
import io.swagger.annotations.ApiModelProperty

case class GetChannel(
                     @ApiModelProperty(value = "Channel identifier.", required = true)
                     @RouteParam id: ChannelId
                    )
