package io.github.cactacea.backend.server.models.requests.user

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, ChannelId}
import io.swagger.annotations.ApiModelProperty

case class DeleteInvitation(
                             @ApiModelProperty(value = "Channel Identifier.", required = true)
                                  @RouteParam channelId: ChannelId,

                             @ApiModelProperty(value = "User Identifier.", required = true)
                                  @RouteParam userId: UserId

                            )
