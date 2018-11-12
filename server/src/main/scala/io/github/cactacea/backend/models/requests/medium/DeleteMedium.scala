package io.github.cactacea.backend.models.requests.medium

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.swagger.annotations.ApiModelProperty

case class DeleteMedium (
                          @ApiModelProperty(value = "Medium identifier.", required = true)
                          @RouteParam id: MediumId
                        )
