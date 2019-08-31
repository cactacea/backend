package io.github.cactacea.backend.server

import com.twitter.finatra.http.routing.HttpRouter
import io.cactacea.finagger.DocsController
import io.github.cactacea.backend.server.utils.modules.AuthenticationModule
import io.github.cactacea.backend.server.utils.swagger.CactaceaSwaggerModule
import io.github.cactacea.backend.server.utils.warmups.CactaceaQueueHandler

class APIServer extends CactaceaServer {

  override def configureHttp(router: HttpRouter): Unit = {
    super.configureHttp(router)
    router
      .add[DocsController]
  }

  addFrameworkModule(CactaceaSwaggerModule)
  addFrameworkModule(AuthenticationModule)

  override def warmup() {
    handle[MigrationHandler]()
    handle[ImageSetupHandler]()
    handle[CactaceaQueueHandler]()
  }

}