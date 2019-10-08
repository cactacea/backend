package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, TweetId, TweetReportId}

case class TweetReports(id: TweetReportId,
                        tweetId: TweetId,
                        by: UserId,
                        reportType: ReportType,
                        reportContent: Option[String],
                        reportedAt: Long
                          )
