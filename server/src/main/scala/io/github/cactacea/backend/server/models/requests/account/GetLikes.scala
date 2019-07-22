package io.github.cactacea.backend.server.models.requests.account

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation.Max
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.swagger.annotations.ApiModelProperty

case class GetLikes(
                     @ApiModelProperty(value = "Account Identifier.", required = true)
                     @RouteParam id: AccountId,

                     @ApiModelProperty(value = "Filters feeds which started on since or later.")
                     @QueryParam since: Option[Long],

                     @ApiModelProperty(value = "The offset of feeds. By default the value is 0.")
                     @QueryParam offset: Option[Int],

                     @ApiModelProperty(value = "Maximum number of entries returned on one result page." +
                       " By default the value is 20 entries. The page size can never be larger than 50.")
                     @QueryParam @Max(50) count: Option[Int]
                     )
