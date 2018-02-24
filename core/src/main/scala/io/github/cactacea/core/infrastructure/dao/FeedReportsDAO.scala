package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._

@Singleton
class FeedReportsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject var identifiesDAO: IdentifiesDAO = _

  def create(feedId: FeedId, reportType: ReportType, sessionId: SessionId): Future[FeedReportId] = {
    for {
      id <- identifiesDAO.create().map(FeedReportId(_))
      _ <- insert(id, feedId, reportType, sessionId)
    } yield (id)
  }

  private def insert(id: FeedReportId, feedId: FeedId, reportType: ReportType, sessionId: SessionId): Future[Long] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedReports].insert(
        _.id            -> lift(id),
        _.feedId        -> lift(feedId),
        _.by            -> lift(by),
        _.reportType    -> lift(reportType.toValue)
      )
    }
    run(q)
  }

  def delete(feedId: FeedId) = {
    val q = quote {
      query[FeedReports]
        .filter(_.feedId == lift(feedId))
        .delete
    }
    run(q).map(_ => true)
  }


}
