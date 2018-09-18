package io.github.cactacea.backend

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.backend.swagger.{BackendDocumentsController, BackendSwaggerModule}
import io.github.cactacea.swagger.DocsController

class DocServer extends BackendServer {

  override def configureHttp(router: HttpRouter) = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
    super.configureHttp(router)
    router
      .add[BackendDocumentsController]
      .add[DocsController]
  }

  addFrameworkModule(BackendSwaggerModule)

}