package io.github.cactacea.backend.models.requests.group

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.GroupInvitationId

case class PostRejectInvitation(
                                 @RouteParam groupInvitationId: GroupInvitationId,
                                 session: Request
                            )
