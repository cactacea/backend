package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.db.DatabaseService
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._

@Singleton
class CommentReportsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject var identifiesDAO: IdentifiesDAO = _

  def create(commentId: CommentId, reportType: ReportType, sessionId: SessionId): Future[CommentReportId] = {
    for {
      id <- identifiesDAO.create().map(CommentReportId(_))
      _ <- insert(id, commentId, reportType, sessionId)
    } yield (id)
  }

  private def insert(id: CommentReportId, commentId: CommentId, reportType: ReportType, sessionId: SessionId): Future[Long] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[CommentReports].insert(
        _.id            -> lift(id),
        _.commentId     -> lift(commentId),
        _.by            -> lift(by),
        _.reportType    -> lift(reportType.toValue)
      )
    }
    run(q)
  }

}
