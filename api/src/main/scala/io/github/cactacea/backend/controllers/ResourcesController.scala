package io.github.cactacea.backend.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.{Request, Status}
import io.github.cactacea.backend.swagger.BackendController
import io.swagger.models.Swagger

@Singleton
class ResourcesController @Inject()(s: Swagger) extends BackendController  {

  protected implicit val swagger = s

  getWithDoc("/assets/:*") { o =>
    o.summary("Get a asset file")
      .tag("Resources")

  } { request: Request =>
    response.ok.file(request.params("*"))
  }

}
