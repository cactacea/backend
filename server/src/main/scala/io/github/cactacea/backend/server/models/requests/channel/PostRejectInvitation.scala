package io.github.cactacea.backend.server.models.requests.channel

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.InvitationId
import io.swagger.annotations.ApiModelProperty

case class PostRejectInvitation(
                                 @ApiModelProperty(value = "Invitation identifier.", required = true)
                                 @RouteParam id: InvitationId
                            )
