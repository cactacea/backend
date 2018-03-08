package io.github.cactacea.backend.models.requests.session

import com.twitter.finatra.request.{Header, QueryParam}

case class GetSignIn(
                   @QueryParam accountName: String,
                   @QueryParam password: String,
                   @QueryParam udid: String,
                   @Header("user-agent") userAgent: String
                  )
