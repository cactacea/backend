package io.github.cactacea.core.application.requests.account

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation._
import io.github.cactacea.core.infrastructure.identifiers.AccountId


case class GetFollowers (
                          @RouteParam accountId: AccountId,
                          @QueryParam since: Option[Long],
                          @QueryParam offset: Option[Int],
                          @QueryParam @Max(50) count: Option[Int],
                          session: Request
                        )