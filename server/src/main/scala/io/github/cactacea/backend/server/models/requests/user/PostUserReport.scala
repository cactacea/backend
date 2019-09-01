package io.github.cactacea.backend.server.models.requests.user

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.swagger.annotations.ApiModelProperty

case class PostUserReport(
                              @ApiModelProperty(value = "User Identifier.", required = true)
                              @RouteParam id: UserId,

                              @ApiModelProperty(value = "Report type.", required = true)
                              reportType: ReportType,

                              @ApiModelProperty(value = "Description about this report.")
                              reportContent: Option[String]
                     )
