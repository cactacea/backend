package io.github.cactacea.backend.server.models.requests.group

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.swagger.annotations.ApiModelProperty

case class PostGroupReport(
                            @ApiModelProperty(value = "Group identifier.", required = true)
                            @RouteParam id: GroupId,

                            @ApiModelProperty(value = "Report type.", required = true)
                            reportType: ReportType,

                            @ApiModelProperty(value = "Description about this report.", required = true)
                            reportContent: Option[String]
                          )
