package io.github.cactacea.backend.models.requests.comment

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.CommentId
import io.swagger.annotations.ApiModelProperty

case class GetComment(
                       @ApiModelProperty(value = "Comment Identifier.")
                       @RouteParam id: CommentId
                       )