package io.github.cactacea.backend.server.controllers

import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.inject.annotations.Flag
import io.github.cactacea.backend.server.utils.authorizations.CactaceaAuthorization.basic
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory
import io.github.cactacea.backend.server.utils.swagger.CactaceaController
import io.swagger.models.Swagger

@Singleton
class ResourcesController @Inject()(@Flag("cactacea.api.prefix") apiPrefix: String,
                                    f: CactaceaAuthenticationFilterFactory,
                                    s: Swagger) extends CactaceaController {

  implicit val swagger: Swagger = s
  implicit val factory: CactaceaAuthenticationFilterFactory = f

  prefix(apiPrefix) {
    scope(basic).get( "/assets/:*") { request: Request =>
      response.ok.file(request.params("*"))
    }
  }

}
