package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.GroupId
import io.swagger.annotations.ApiModelProperty

case class PostLeaveGroup(
                           @ApiModelProperty(value = "Group invitation identifier.")
                           @RouteParam id: GroupId
                        )
