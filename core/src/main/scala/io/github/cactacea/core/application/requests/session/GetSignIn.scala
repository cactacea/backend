package io.github.cactacea.core.application.requests.session

import com.twitter.finagle.http.Request
import com.twitter.finatra.request.{Header, QueryParam}

case class GetSignIn(
                   @QueryParam accountName: String,
                   @QueryParam password: String,
                   @QueryParam udid: String,
                   @Header("user-agent") userAgent: String,
                   session: Request
                  )
