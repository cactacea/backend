package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers.AccountId
import io.swagger.annotations.ApiModelProperty

case class PostAccountReport(
                              @ApiModelProperty(value = "Account Identifier.")
                              @RouteParam id: AccountId,

                              reportType: ReportType
                     )
