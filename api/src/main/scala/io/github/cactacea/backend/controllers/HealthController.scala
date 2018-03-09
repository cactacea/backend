package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import io.swagger.models.Swagger

@Singleton
class HealthController @Inject()(s: Swagger) extends Controller with SwaggerController {

  implicit protected val swagger = s

  getWithDoc("/ping") { o =>
    o.summary("Health checking")
      .tag("System")
      .responseWith(200, "Service is operating normally")
  } { (_: Request) =>
    response.ok
  }

}
