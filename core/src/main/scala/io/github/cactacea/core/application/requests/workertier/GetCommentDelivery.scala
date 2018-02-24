package io.github.cactacea.core.application.requests.workertier

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.CommentId

case class GetCommentDelivery(@RouteParam commentId: CommentId)
