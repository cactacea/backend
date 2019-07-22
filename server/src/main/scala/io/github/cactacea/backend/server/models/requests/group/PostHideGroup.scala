package io.github.cactacea.backend.server.models.requests.group

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.swagger.annotations.ApiModelProperty

case class PostHideGroup (
                           @ApiModelProperty(value = "Group identifier.", required = true)
                           @RouteParam id: GroupId
                       )
