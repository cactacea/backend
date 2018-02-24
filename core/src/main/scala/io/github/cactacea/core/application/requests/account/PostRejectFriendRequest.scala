package io.github.cactacea.core.application.requests.account

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.FriendRequestId

case class PostRejectFriendRequest (
                                     @RouteParam friendRequestId: FriendRequestId,
                                     session: Request
                            )
