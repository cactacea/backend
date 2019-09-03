package io.github.cactacea.backend.server.models.requests.user

import com.twitter.finatra.request.RouteParam
import io.swagger.annotations.ApiModelProperty

case class GetUserName(
                           @ApiModelProperty(value = "User name.", required = true)
                           @RouteParam userName: String
                     )
