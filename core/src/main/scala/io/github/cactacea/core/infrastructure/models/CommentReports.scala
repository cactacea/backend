package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.ReportType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, CommentId, CommentReportId}

case class CommentReports(id: CommentReportId,
                          commentId: CommentId,
                          by: AccountId,
                          reportType: ReportType
                       )
