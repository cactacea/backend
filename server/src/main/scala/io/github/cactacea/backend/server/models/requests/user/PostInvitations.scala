package io.github.cactacea.backend.server.models.requests.user

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, ChannelId}
import io.swagger.annotations.ApiModelProperty

case class PostInvitations(
                            @ApiModelProperty(value = "Channel Identifier.", required = true)
                                   @RouteParam id: ChannelId,

                            @ApiModelProperty(value = "User Identifies.")
                                   userIds: Array[UserId]
                                 )
