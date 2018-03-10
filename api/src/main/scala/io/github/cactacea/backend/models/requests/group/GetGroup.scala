package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class GetGroup(
                     @RouteParam id: GroupId
                    )
