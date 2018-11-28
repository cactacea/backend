package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, CommentId, CommentReportId}

case class CommentReports(id: CommentReportId,
                          commentId: CommentId,
                          by: AccountId,
                          reportType: ReportType,
                          reportContent: Option[String],
                          reportedAt: Long
                         )
