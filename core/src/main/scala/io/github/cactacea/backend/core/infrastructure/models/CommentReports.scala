package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, CommentId, CommentReportId}

case class CommentReports(id: CommentReportId,
                          commentId: CommentId,
                          by: UserId,
                          reportType: ReportType,
                          reportContent: Option[String],
                          reportedAt: Long
                         )
