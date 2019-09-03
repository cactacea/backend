package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, ChannelId, ChannelReportId}

case class ChannelReports(id: ChannelReportId,
                          channelId: ChannelId,
                          by: UserId,
                          reportType: ReportType,
                          reportContent: Option[String],
                          reportedAt: Long
                        )
