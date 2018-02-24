package io.github.cactacea.core.application.requests.account

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{QueryParam}
import com.twitter.finatra.validation._

case class GetAccounts(
                        @QueryParam @Size(min = 0, max = 1000) displayName: Option[String],
                        @QueryParam since: Option[Long],
                        @QueryParam offset: Option[Int],
                        @QueryParam @Max(50) count: Option[Int],
                        session: Request
                      )