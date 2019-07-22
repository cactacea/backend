package io.github.cactacea.backend.server.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.FriendRequestId
import io.swagger.annotations.ApiModelProperty

case class PostRejectFriendRequest (
                                     @ApiModelProperty(value = "Friend request Identifier.", required = true)
                                     @RouteParam id: FriendRequestId
                            )
