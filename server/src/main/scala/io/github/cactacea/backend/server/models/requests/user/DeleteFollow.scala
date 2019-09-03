package io.github.cactacea.backend.server.models.requests.user

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.swagger.annotations.ApiModelProperty

case class DeleteFollow(
                            @ApiModelProperty(value = "User Identifier.", required = true)
                            @RouteParam id: UserId
                       )
