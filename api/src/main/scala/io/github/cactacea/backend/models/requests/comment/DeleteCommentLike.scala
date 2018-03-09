package io.github.cactacea.backend.models.requests.comment

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.CommentId

case class DeleteCommentLike(
                                  @RouteParam commentId: CommentId
                               )

