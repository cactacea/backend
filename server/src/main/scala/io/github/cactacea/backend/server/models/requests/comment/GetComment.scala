package io.github.cactacea.backend.server.models.requests.comment

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.swagger.annotations.ApiModelProperty

case class GetComment(
                       @ApiModelProperty(value = "Comment Identifier.", required = true)
                       @RouteParam id: CommentId
                       )
