package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.swagger.models.Swagger

@Singleton
class HealthController @Inject()(s: Swagger, c: ConfigService) extends BackendController {

  protected implicit val swagger = s
  getWithDoc(c.rootPath + "/ping") { o =>
    o.summary("Health checking")
      .tag("System")
      .responseWith(200, "Service is operating normally")
  } { (_: Request) =>
    response.ok("pong")
  }

}
