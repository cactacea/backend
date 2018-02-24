package io.github.cactacea.core.application.requests.session

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{Header, QueryParam}
import com.twitter.finatra.validation.Size

case class GetTwitterSignIn(
                          @QueryParam @Size(min = 1, max = 1000) accessTokenKey: String,
                          @QueryParam @Size(min = 1, max = 1000) accessTokenSecret: String,
                          @QueryParam @Size(min = 1, max = 1000) udid: String,
                          @Header("user-agent") userAgent: String,
                          session: Request
                 )
