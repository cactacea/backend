package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FeedReportsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(feedId: FeedId, reportType: ReportType, sessionId: SessionId): Future[FeedReportId] = {
    for {
      id <- insert(feedId, reportType, sessionId)
    } yield (id)
  }

  private def insert(feedId: FeedId, reportType: ReportType, sessionId: SessionId): Future[FeedReportId] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FeedReports].insert(
        _.feedId        -> lift(feedId),
        _.by            -> lift(by),
        _.reportType    -> lift(reportType)
      ).returning(_.id)
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
