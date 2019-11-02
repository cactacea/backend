package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class ChannelReportsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(channelId: ChannelId, reportType: ReportType, reportContent: Option[String], sessionId: SessionId): Future[ChannelReportId] = {
    val reportedAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[ChannelReports].insert(
        _.channelId       -> lift(channelId),
        _.by            -> lift(by),
        _.reportType    -> lift(reportType),
        _.reportContent -> lift(reportContent),
        _.reportedAt    -> lift(reportedAt)
      ).returningGenerated(_.id)
    }
    run(q)
  }

}
