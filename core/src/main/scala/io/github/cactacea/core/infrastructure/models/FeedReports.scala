package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{FeedId, FeedReportId, AccountId}

case class FeedReports(id: FeedReportId,
                       feedId: FeedId,
                       by: AccountId,
                       reportType: Long
                          )
