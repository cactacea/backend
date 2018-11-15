package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.swagger.annotations.ApiModelProperty

case class GetGroup(
                     @ApiModelProperty(value = "Group identifier.", required = true)
                     @RouteParam id: GroupId
                    )
