package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import io.github.cactacea.backend.swagger.BackendController
import io.swagger.models.Swagger

@Singleton
class HealthController @Inject()(s: Swagger) extends BackendController {

  protected implicit val swagger = s
  getWithDoc("/ping") { o =>
    o.summary("Health checking")
      .tag("System")
      .responseWith(200, "Service is operating normally")
  } { (_: Request) =>
    response.ok("pong")
  }

}
