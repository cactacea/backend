package io.github.cactacea.backend.server.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.swagger.annotations.ApiModelProperty

case class GetAccountName(
                           @ApiModelProperty(value = "Account name.", required = true)
                           @RouteParam accountName: String
                     )