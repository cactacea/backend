package io.github.cactacea.backend.models.requests.comment

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.swagger.annotations.ApiModelProperty

case class DeleteCommentLike(
                              @ApiModelProperty(value = "Comment Identifier.")
                              @RouteParam id: CommentId
                               )

