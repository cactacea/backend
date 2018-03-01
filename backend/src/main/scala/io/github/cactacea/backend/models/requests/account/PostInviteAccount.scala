package io.github.cactacea.backend.models.requests.account

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.{GroupId, AccountId}

case class PostInviteAccount(
                                @RouteParam accountId: AccountId,
                                @RouteParam groupId: GroupId,
                                session: Request
                            )
