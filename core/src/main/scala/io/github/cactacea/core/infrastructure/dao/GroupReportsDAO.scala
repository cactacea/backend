package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DatabaseService
import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._

@Singleton
class GroupReportsDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var identifyService: IdentifyService = _

  def create(groupId: GroupId, reportType: ReportType, sessionId: SessionId): Future[GroupReportId] = {
    for {
      id <- identifyService.generate().map(GroupReportId(_))
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
        _.reportType    -> lift(reportType)
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
