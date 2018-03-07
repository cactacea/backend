package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class FeedReportsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _

  def create(feedId: FeedId, reportType: ReportType, sessionId: SessionId): Future[FeedReportId] = {
    for {
      id <- identifyService.generate().map(FeedReportId(_))
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
        _.reportType    -> lift(reportType)
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
