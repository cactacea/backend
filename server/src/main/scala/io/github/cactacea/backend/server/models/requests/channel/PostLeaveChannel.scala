package io.github.cactacea.backend.server.models.requests.channel

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId
import io.swagger.annotations.ApiModelProperty

case class PostLeaveChannel(
                           @ApiModelProperty(value = "Invitation identifier.", required = true)
                           @RouteParam id: ChannelId
                        )
