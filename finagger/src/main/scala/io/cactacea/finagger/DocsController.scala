package io.cactacea.finagger

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.response.Mustache
import com.twitter.inject.annotations.Flag
import io.github.cactacea.finagger.BuildInfo
import io.swagger.models.Swagger
import io.swagger.util.Json

@Mustache("redoc")
case class Redoc(modelPath: String)

@Singleton
class DocsController @Inject()(swagger: Swagger, @Flag("swagger.docs.endpoint") docPath: String) extends Controller {

  get(s"${docPath}/model") { request: Request =>
    response.ok.body(Json.mapper.writeValueAsString(swagger).replace("""$"""", """""""))
      .contentType("application/json").toFuture
  }

  get(s"${docPath}/ui") { request: Request =>
    response.temporaryRedirect
      .location(s"../../webjars/swagger-ui//${BuildInfo.swaggerUIVersion}/index.html?url=/$docPath/model")
  }

  get(s"$docPath/redoc") { request: Request =>
    Redoc(modelPath = s"$docPath/model")
  }
}
