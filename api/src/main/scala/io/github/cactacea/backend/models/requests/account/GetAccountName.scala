package io.github.cactacea.backend.models.requests.account

import com.twitter.finatra.request.RouteParam

case class GetAccountName(
                           @RouteParam accountName: String
                     )
