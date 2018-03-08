package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import com.twitter.finatra.validation.Size
import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class PutAccount(
                    @RouteParam accountId: AccountId,
                    @Size(min = 1, max = 1000) displayName: Option[String]
                  )
