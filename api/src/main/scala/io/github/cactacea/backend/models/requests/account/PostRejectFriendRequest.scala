package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.FriendRequestId

case class PostRejectFriendRequest (
                                     @RouteParam friendRequestId: FriendRequestId
                            )
