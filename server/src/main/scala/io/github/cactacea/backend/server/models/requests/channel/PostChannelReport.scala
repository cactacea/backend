package io.github.cactacea.backend.server.models.requests.channel

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId
import io.swagger.annotations.ApiModelProperty

case class PostChannelReport(
                            @ApiModelProperty(value = "Channel identifier.", required = true)
                            @RouteParam id: ChannelId,

                            @ApiModelProperty(value = "Report type.", required = true)
                            reportType: ReportType,

                            @ApiModelProperty(value = "Description about this report.", required = true)
                            reportContent: Option[String]
                          )
