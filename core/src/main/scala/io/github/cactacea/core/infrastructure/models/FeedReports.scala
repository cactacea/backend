package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId, FeedReportId}

case class FeedReports(id: FeedReportId,
                       feedId: FeedId,
                       by: AccountId,
                       reportType: ReportType
                          )
