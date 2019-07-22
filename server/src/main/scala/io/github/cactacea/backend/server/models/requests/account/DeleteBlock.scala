package io.github.cactacea.backend.server.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.swagger.annotations.ApiModelProperty

case class DeleteBlock (
                         @ApiModelProperty(value = "Account identifier.", required = true)
                         @RouteParam id: AccountId
                       )
