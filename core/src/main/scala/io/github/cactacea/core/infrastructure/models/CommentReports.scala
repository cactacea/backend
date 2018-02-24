package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{CommentId, CommentReportId, AccountId}

case class CommentReports(id: CommentReportId,
                          commentId: CommentId,
                          by: AccountId,
                          reportType: Long
                       )
