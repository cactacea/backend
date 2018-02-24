package io.github.cactacea.core.application.requests.group

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finagle.http.Request
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers.GroupId

case class PostGroupReport(
                            @RouteParam groupId: GroupId,
                            reportType: ReportType,
                            session: Request
                                )
