package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.swagger.annotations.ApiModelProperty

case class PostAccountReport(
                              @ApiModelProperty(value = "Account Identifier.", required = true)
                              @RouteParam id: AccountId,

                              @ApiModelProperty(value = "Report type.", required = true)
                              reportType: ReportType,

                              @ApiModelProperty(value = "Description about this report.")
                              reportContent: Option[String]
                     )
