package io.github.cactacea.backend.models.requests.setting

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.validation.Size


case class PostSocialAccount(
                              @RouteParam socialAccountType: String,
                              @QueryParam @Size(min = 1, max = 1000) accessTokenKey: String,
                              @QueryParam @Size(min = 1, max = 1000) accessTokenSecret: String,
                              session: Request
                                 )
