package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max

case class GetSessionFollowing(
                       @QueryParam since: Option[Long],
                       @QueryParam offset: Option[Int],
                       @QueryParam @Max(50) count: Option[Int]
                     )
