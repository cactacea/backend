package io.github.cactacea.backend.server.models.requests.user

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.swagger.annotations.ApiModelProperty

case class GetUserStatus(
                       @ApiModelProperty(value = "User identifier.", required = true)
                       @RouteParam id: UserId
                     )
