package io.github.cactacea.core.application.requests.account

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam

case class GetAccountName(
                           @RouteParam accountName: String,
                           session: Request
                     )
