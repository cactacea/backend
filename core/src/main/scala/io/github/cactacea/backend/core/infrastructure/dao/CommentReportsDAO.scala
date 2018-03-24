package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.IdentifyService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class CommentReportsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _

  def create(commentId: CommentId, reportType: ReportType, sessionId: SessionId): Future[CommentReportId] = {
    for {
      id <- identifyService.generate().map(CommentReportId(_))
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
        _.reportType    -> lift(reportType)
      )
    }
    run(q)
  }

}
