package io.github.cactacea.backend.models.requests.comment

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class GetComments(
                        @QueryParam feedId: FeedId,
                        @QueryParam since: Option[Long],
                        @QueryParam offset: Option[Int],
                        @QueryParam @Max(50) count: Option[Int]
                       )
