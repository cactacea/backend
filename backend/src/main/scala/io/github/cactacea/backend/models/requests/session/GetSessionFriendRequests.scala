package io.github.cactacea.backend.models.requests.session

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.Max

case class GetSessionFriendRequests(
                              @QueryParam since: Option[Long],
                              @QueryParam offset: Option[Int],
                              @QueryParam @Max(50) count: Option[Int],
                              @QueryParam received: Boolean,
                              session: Request
                            )
