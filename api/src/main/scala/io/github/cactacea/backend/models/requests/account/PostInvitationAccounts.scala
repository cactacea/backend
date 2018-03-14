package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.{QueryParam, RouteParam}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId}

case class PostInvitationAccounts(
                                   @RouteParam groupId: GroupId,
                                   @QueryParam accountIds: Array[AccountId]
                                 )
