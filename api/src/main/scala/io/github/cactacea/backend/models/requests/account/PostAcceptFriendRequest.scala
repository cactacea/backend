package io.github.cactacea.backend.models.requests.account

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.FriendRequestId

case class PostAcceptFriendRequest (
                                     @RouteParam friendRequestId: FriendRequestId,
                                     session: Request
                            )
