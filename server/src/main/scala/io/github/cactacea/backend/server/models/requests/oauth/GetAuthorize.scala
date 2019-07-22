package io.github.cactacea.backend.server.models.requests.oauth

import com.twitter.finatra.request.RouteParam

case class GetAuthorize(
                         @RouteParam responseType: String,
                         @RouteParam clientId: String,
                         @RouteParam redirectUri: Option[String],
                         @RouteParam scope: Option[String]
                       )
