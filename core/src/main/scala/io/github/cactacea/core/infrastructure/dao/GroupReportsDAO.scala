package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class GroupReportsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject var identifiesDAO: IdentifiesDAO = _

  def create(groupId: GroupId, reportType: ReportType, sessionId: SessionId): Future[GroupReportId] = {
    for {
      id <- identifiesDAO.create().map(GroupReportId(_))
      _ <- insert(id, groupId, reportType, sessionId)
    } yield (id)
  }

  private def insert(id: GroupReportId, groupId: GroupId, reportType: ReportType, sessionId: SessionId): Future[Long] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[GroupReports].insert(
        _.id            -> lift(id),
        _.groupId       -> lift(groupId),
        _.by            -> lift(by),
        _.reportType    -> lift(reportType.toValue)
      )
    }
    run(q)
  }

  def delete(groupId: GroupId): Future[Boolean] = {
    val q = quote {
      query[GroupReports]
        .filter(_.groupId == lift(groupId))
        .delete
    }
    run(q).map(_ >= 0)
  }

}
