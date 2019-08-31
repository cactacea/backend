package io.github.cactacea.finagger

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.inject.annotations.Flag
import io.swagger.models.Swagger
import io.swagger.util.Json

@Singleton
class DocsController @Inject()(swagger: Swagger, @Flag("swagger.docs.endpoint") docPath: String) extends Controller {

  get(s"${docPath}/model") { _: Request =>
    response.ok.body(Json.mapper.writeValueAsString(swagger).replace("""$"""", """""""))
      .contentType("application/json").toFuture
  }

  get(s"${docPath}/ui") { _: Request =>
    response.temporaryRedirect
      .location(s"../../webjars/swagger-ui//${BuildInfo.swaggerUIVersion}/index.html?url=/$docPath/model")
  }

  get(s"$docPath/redoc") { _: Request =>
    Redoc(modelPath = s"$docPath/model")
  }
}
