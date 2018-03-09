package io.github.cactacea.backend.swagger

import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import io.swagger.models.Swagger
import io.swagger.util.Yaml

class BackendDocumentsController @Inject()(swagger: Swagger) extends Controller {

  get("/swagger.yaml") { _: Request =>
    response
      .ok(Yaml.mapper.writeValueAsString(swagger))
      .contentTypeJson
  }

}
