package io.github.cactacea.core.application.controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class ResourcesController extends Controller  {

  get("/assets/:*") { request: Request =>
    response.ok.file(request.params("*"))
  }

}
