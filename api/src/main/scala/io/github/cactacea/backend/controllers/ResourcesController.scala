package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import io.github.cactacea.backend.swagger.BackendController
import io.github.cactacea.core.application.components.interfaces.ConfigService
import io.swagger.models.Swagger

@Singleton
class ResourcesController @Inject()(s: Swagger, c: ConfigService) extends BackendController  {

  protected implicit val swagger = s

  getWithDoc(c.rootPath + "/assets/:*") { o =>
    o.summary("Get a asset file")
      .tag("Resources")

  } { request: Request =>
    response.ok.file(request.params("*"))
  }

}
