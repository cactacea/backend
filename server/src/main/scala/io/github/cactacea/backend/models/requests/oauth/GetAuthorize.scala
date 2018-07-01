package io.github.cactacea.backend.models.requests.oauth

import com.twitter.finatra.request.RouteParam

case class GetAuthorize(
                         @RouteParam username: Option[String],
                         @RouteParam password: Option[String],
                         @RouteParam responseType: String,
                         @RouteParam clientId: String,
                         @RouteParam scope: Option[String]
                       )
