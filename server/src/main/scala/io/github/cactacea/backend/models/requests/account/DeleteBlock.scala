package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.swagger.annotations.ApiModelProperty

case class DeleteBlock (
                         @ApiModelProperty(value = "Account identifier.")
                         @RouteParam id: AccountId
                       )