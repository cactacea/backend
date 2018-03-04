package io.github.cactacea.backend.controllers

import com.google.inject.Singleton
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

@Singleton
class HealthController extends Controller {

  get("/check") { request: Request =>
    response.ok
  }

}
