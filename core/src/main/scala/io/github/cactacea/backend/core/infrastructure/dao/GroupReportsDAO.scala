package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class GroupReportsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(groupId: GroupId, reportType: ReportType, sessionId: SessionId): Future[GroupReportId] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[GroupReports].insert(
        _.groupId       -> lift(groupId),
        _.by            -> lift(by),
        _.reportType    -> lift(reportType)
      ).returning(_.id)
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
