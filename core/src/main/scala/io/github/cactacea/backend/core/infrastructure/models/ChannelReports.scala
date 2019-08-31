package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.ReportType
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, ChannelId, ChannelReportId}

case class ChannelReports(id: ChannelReportId,
                          channelId: ChannelId,
                          by: AccountId,
                          reportType: ReportType,
                          reportContent: Option[String],
                          reportedAt: Long
                        )
