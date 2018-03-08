package io.github.cactacea.backend.models.requests.group

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class PostHideGroup (
                           @RouteParam groupId: GroupId,
                           session: Request
                       )
