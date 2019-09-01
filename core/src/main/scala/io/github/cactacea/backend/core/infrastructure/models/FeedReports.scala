package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, FeedId, FeedReportId}

case class FeedReports(id: FeedReportId,
                       feedId: FeedId,
                       by: UserId,
                       reportType: ReportType,
                       reportContent: Option[String],
                       reportedAt: Long
                          )
