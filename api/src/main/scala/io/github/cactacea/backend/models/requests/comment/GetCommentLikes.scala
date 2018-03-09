package io.github.cactacea.backend.models.requests.comment

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation.Max
import io.github.cactacea.core.infrastructure.identifiers.CommentId

case class GetCommentLikes(
                                @RouteParam commentId: CommentId,
                                @QueryParam since: Option[Long],
                                @QueryParam offset: Option[Int],
                                @QueryParam @Max(50) count: Option[Int]
                           )

