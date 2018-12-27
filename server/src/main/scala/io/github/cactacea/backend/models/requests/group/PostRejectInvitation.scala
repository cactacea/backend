package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupInvitationId
import io.swagger.annotations.ApiModelProperty

case class PostRejectInvitation(
                                 @ApiModelProperty(value = "Group groupInvitation identifier.", required = true)
                                 @RouteParam id: GroupInvitationId
                            )
