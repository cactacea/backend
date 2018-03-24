package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import com.twitter.finatra.validation.Size
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.swagger.annotations.ApiModelProperty

case class PutAccountDisplayName(
                                  @ApiModelProperty(value = "Account Identifier.")
                                  @RouteParam id: AccountId,

                                  @ApiModelProperty(value = "Display name that each account shown.")
                                  @Size(min = 1, max = 1000) displayName: Option[String]
                  )
