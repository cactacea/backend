package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, AccountReportId}

case class AccountReports(id: AccountReportId,
                          accountId: AccountId,
                          by: AccountId,
                          reportType: ReportType
                       )
