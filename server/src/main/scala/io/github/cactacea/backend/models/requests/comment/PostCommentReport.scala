package io.github.cactacea.backend.models.requests.comment

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.infrastructure.identifiers.CommentId
import io.swagger.annotations.ApiModelProperty

case class PostCommentReport(
                              @ApiModelProperty(value = "Comment Identifier.")
                              @RouteParam id: CommentId,

                              reportType: ReportType
                     )
