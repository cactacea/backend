package io.github.cactacea.backend.models.requests.comment

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers.CommentId

case class PostCommentReport(
                              @RouteParam commentId: CommentId,
                              reportType: ReportType
                     )
