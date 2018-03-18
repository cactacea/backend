package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.AccountId
import io.swagger.annotations.ApiModelProperty

case class PostMute (
                      @ApiModelProperty(value = "Account Identifier.")
                      @RouteParam id: AccountId
                     )
