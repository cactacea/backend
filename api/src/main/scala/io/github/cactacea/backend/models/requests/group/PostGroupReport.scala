package io.github.cactacea.backend.models.requests.group

import com.twitter.finatra.request.RouteParam
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class PostGroupReport(
                            @RouteParam groupId: GroupId,
                            reportType: ReportType
                                )
