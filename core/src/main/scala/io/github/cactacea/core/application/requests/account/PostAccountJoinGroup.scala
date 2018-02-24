package io.github.cactacea.core.application.requests.account

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.{GroupId, AccountId}

case class PostAccountJoinGroup(
                              @RouteParam accountId: AccountId,
                              @RouteParam groupId: GroupId,
                              session: Request
                         )
