package io.github.cactacea.core.application.requests.account

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{FormParam, RouteParam}
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class PostAccountReport(
                              @RouteParam accountId: AccountId,
                              reportType: ReportType,
                              session: Request
                     )
