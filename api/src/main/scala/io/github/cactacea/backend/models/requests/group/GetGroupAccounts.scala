package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation.Max
import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class GetGroupAccounts(
                             @RouteParam id: GroupId,
                             @QueryParam since: Option[Long],
                             @QueryParam offset: Option[Int],
                             @QueryParam @Max(50) count: Option[Int]
                      )