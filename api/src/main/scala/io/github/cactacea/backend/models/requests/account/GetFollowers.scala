package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation._
import io.github.cactacea.core.infrastructure.identifiers.AccountId


case class GetFollowers (
                          @RouteParam id: AccountId,
                          @QueryParam since: Option[Long],
                          @QueryParam offset: Option[Int],
                          @QueryParam @Max(50) count: Option[Int]
                        )