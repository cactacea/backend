package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, GroupId, GroupReportId}

case class GroupReports(id: GroupReportId,
                        groupId: GroupId,
                        by: AccountId,
                        reportType: ReportType
                        )
