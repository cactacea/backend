package io.github.cactacea.core.application.requests.group

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.GroupInviteId

case class PostRejectInvite(
                                  @RouteParam groupInviteId: GroupInviteId,
                                  session: Request
                            )
