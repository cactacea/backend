package io.github.cactacea.backend.models.requests.auth

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.RouteParam

case class PostOAuthToken(
                        @RouteParam username: String,
                        @RouteParam password: String,
                        @RouteParam responseType: String,
                        @RouteParam clientId: String,
                        @RouteParam redirectUri: String,
                        @RouteParam scope: Option[String],
                        @RouteParam state: Option[String],
                        @RouteParam codeChallenge: Option[String],
                        @RouteParam codeChallengeMethod: Option[String],
                        request: Request
                      )
