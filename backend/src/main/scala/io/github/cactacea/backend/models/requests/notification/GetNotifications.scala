package io.github.cactacea.backend.models.requests.notification

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max

case class GetNotifications(
                        @QueryParam since: Option[Long],
                        @QueryParam offset: Option[Int],
                        @QueryParam @Max(50) count: Option[Int],
                        session: Request
                      )
