package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class AccountReportsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[AccountReportId] = {
    val reportedAt = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountReports].insert(
        _.accountId     -> lift(accountId),
        _.by            -> lift(by),
        _.reportType    -> lift(reportType),
        _.reportContent -> lift(reportContent),
        _.reportedAt    -> lift(reportedAt)
      ).returning(_.id)
    }
    run(q)
  }

}
