package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class GetAccountGroup(
                      @RouteParam id: AccountId
                    )