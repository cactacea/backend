package io.github.cactacea.backend.models.requests.feed

import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class GetFeeds(
                     @QueryParam id: AccountId,
                     @QueryParam since: Option[Long],
                     @QueryParam offset: Option[Int],
                     @QueryParam @Max(50) count: Option[Int]
                      )
