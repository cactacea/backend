package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, GroupId, GroupReportId}

case class GroupReports(id: GroupReportId,
                        groupId: GroupId,
                        by: AccountId,
                        reportType: ReportType,
                        reportContent: Option[String]
                        )
