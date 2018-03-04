package io.github.cactacea.backend.models.requests.comment

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.CommentId

case class DeleteComment(
                          @RouteParam commentId: CommentId,
                          session: Request
                     )