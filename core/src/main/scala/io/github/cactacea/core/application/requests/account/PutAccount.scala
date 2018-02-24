package io.github.cactacea.core.application.requests.account

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{FormParam, RouteParam}
import com.twitter.finatra.validation.Size
import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class PutAccount(
                    @RouteParam accountId: AccountId,
                    @Size(min = 1, max = 1000) displayName: Option[String],
                    session: Request
                  )
