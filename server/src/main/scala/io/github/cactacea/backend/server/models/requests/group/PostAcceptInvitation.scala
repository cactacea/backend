package io.github.cactacea.backend.server.models.requests.group

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.InvitationId
import io.swagger.annotations.ApiModelProperty

case class PostAcceptInvitation(
                                 @ApiModelProperty(value = "Invitation identifier.", required = true)
                                 @RouteParam id: InvitationId
                               )
