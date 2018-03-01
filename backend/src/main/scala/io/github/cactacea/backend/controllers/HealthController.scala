package io.github.cactacea.backend.controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class HealthController extends Controller {

  get("/check") { request: Request =>
    response.ok
  }

}
