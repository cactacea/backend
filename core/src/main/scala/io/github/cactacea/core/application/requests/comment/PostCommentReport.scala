package io.github.cactacea.core.application.requests.comment

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers.CommentId

case class PostCommentReport(
                              @RouteParam commentId: CommentId,
                              reportType: ReportType,
                              session: Request
                     )
