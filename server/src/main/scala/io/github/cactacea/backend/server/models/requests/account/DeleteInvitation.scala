package io.github.cactacea.backend.server.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, ChannelId}
import io.swagger.annotations.ApiModelProperty

case class DeleteInvitation(
                             @ApiModelProperty(value = "Channel Identifier.", required = true)
                                  @RouteParam channelId: ChannelId,

                             @ApiModelProperty(value = "Account Identifier.", required = true)
                                  @RouteParam accountId: AccountId

                            )
