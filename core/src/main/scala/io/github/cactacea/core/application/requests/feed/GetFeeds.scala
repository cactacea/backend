package io.github.cactacea.core.application.requests.feed

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max
import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class GetFeeds(
                     @QueryParam accountId: AccountId,
                     @QueryParam since: Option[Long],
                     @QueryParam offset: Option[Int],
                     @QueryParam @Max(50) count: Option[Int],
                     session: Request
                      )
