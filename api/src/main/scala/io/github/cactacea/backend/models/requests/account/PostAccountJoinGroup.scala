package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId}

case class PostAccountJoinGroup(
                                 @RouteParam accountId: AccountId,
                                 @RouteParam groupId: GroupId
                         )
