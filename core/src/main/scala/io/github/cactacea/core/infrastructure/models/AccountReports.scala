package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, AccountReportId}

case class AccountReports(id: AccountReportId,
                          accountId: AccountId,
                          by: AccountId,
                          reportType: ReportType
                       )
