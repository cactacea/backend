package io.github.cactacea.backend.models.requests.account

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class DeleteFriendRequest(
                              @RouteParam accountId: AccountId,
                              session: Request
                       )
