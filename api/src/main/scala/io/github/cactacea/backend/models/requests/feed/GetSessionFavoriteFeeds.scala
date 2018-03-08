package io.github.cactacea.backend.models.requests.feed

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max

case class GetSessionFavoriteFeeds(
                         @QueryParam since: Option[Long],
                         @QueryParam offset: Option[Int],
                         @QueryParam @Max(50) count: Option[Int]
                       )
