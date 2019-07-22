package io.github.cactacea.backend.server.controllers

import com.google.inject.Singleton
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

@Singleton
class ResourcesController extends Controller  {

  get( "/assets/:*") { request: Request =>
    response.ok.file(request.params("*"))
  }

}
