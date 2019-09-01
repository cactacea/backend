package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, UserReportId}

case class UserReports(id: UserReportId,
                       userId: UserId,
                       by: UserId,
                       reportType: ReportType,
                       reportContent: Option[String],
                       reportedAt: Long
                       )
