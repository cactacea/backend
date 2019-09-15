package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class TweetReportsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(tweetId: TweetId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[TweetReportId] = {
    val reportedAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[TweetReports].insert(
        _.tweetId        -> lift(tweetId),
        _.by            -> lift(by),
        _.reportType    -> lift(reportType),
        _.reportContent -> lift(reportContent),
        _.reportedAt    -> lift(reportedAt)
      ).returning(_.id)
    }
    run(q)
  }

  def delete(tweetId: TweetId): Future[Unit] = {
    val q = quote {
      query[TweetReports]
        .filter(_.tweetId == lift(tweetId))
        .delete
    }
    run(q).map(_ => ())
  }

}
