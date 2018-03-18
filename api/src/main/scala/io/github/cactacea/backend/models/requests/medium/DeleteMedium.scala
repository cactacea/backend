package io.github.cactacea.backend.models.requests.medium

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.MediumId
import io.swagger.annotations.ApiModelProperty

case class DeleteMedium (
                          @ApiModelProperty(value = "Medium identifier.")
                          @RouteParam id: MediumId
                        )
