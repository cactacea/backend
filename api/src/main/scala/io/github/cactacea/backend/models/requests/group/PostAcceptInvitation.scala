package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.GroupInvitationId
import io.swagger.annotations.ApiModelProperty

case class PostAcceptInvitation(
                                 @ApiModelProperty(value = "Group invitation identifier.")
                                 @RouteParam id: GroupInvitationId
                               )
