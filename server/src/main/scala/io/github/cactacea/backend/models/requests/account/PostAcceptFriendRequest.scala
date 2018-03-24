package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.FriendRequestId
import io.swagger.annotations.ApiModelProperty

case class PostAcceptFriendRequest (
                                     @ApiModelProperty(value = "Friend request Identifier.")
                                     @RouteParam id: FriendRequestId
                            )
