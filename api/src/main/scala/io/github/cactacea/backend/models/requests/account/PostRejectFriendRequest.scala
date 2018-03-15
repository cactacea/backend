package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.FriendRequestId
import io.swagger.annotations.ApiModelProperty

case class PostRejectFriendRequest (
                                     @ApiModelProperty(value = "Friend request Identifier.")
                                     @RouteParam id: FriendRequestId
                            )
