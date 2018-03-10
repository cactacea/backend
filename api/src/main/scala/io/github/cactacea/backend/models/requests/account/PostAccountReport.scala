package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers.AccountId

case class PostAccountReport(
                              @RouteParam id: AccountId,
                              reportType: ReportType
                     )
