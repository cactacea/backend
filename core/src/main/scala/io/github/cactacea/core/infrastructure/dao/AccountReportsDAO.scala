package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._

@Singleton
class AccountReportsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _

  def find(accountId: AccountId): Future[List[AccountReports]] = {
    val q = quote {
      query[AccountReports]
        .filter(_.accountId == lift(accountId))
    }
    run(q)
  }

  def create(accountId: AccountId, reportType: ReportType, sessionId: SessionId): Future[AccountReportId] = {
    for {
      id <- identifyService.generate().map(AccountReportId(_))
      _ <- insert(id, accountId, reportType, sessionId)
    } yield (id)
  }

  private def insert(id: AccountReportId, accountId: AccountId, reportType: ReportType, sessionId: SessionId): Future[Long] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[AccountReports].insert(
        _.id          -> lift(id),
        _.accountId   -> lift(accountId),
        _.by          -> lift(by),
        _.reportType  -> lift(reportType)
      )
    }
    run(q)
  }

}
