package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.swagger.SwaggerController
import io.github.cactacea.backend.utils.oauth.OAuthController
import io.swagger.models.Swagger

@Singleton
class HealthController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String, s: Swagger)
  extends SwaggerController with OAuthController {

  implicit val swagger: Swagger = s

  prefix(apiPrefix) {

    getWithDoc("/ping") { o =>
      o.summary("Health checking")
        .tag("System")
        .responseWith(200, "Service is operating normally")
    } { (_: Request) =>
      response.ok("pong")
    }

  }

}
