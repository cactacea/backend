package io.github.cactacea.backend.server

import com.twitter.finatra.http.routing.HttpRouter
import io.github.cactacea.backend.server.utils.swagger.CactaceaSwaggerModule
import io.github.cactacea.backend.server.utils.warmups.CactaceaQueueHandler
import io.github.cactacea.finagger.DocsController

class APIServer extends CactaceaServer {

  override def configureHttp(router: HttpRouter): Unit = {
    super.configureHttp(router)
    router
      .add[DocsController]
  }

  addFrameworkModule(CactaceaSwaggerModule)

  override def warmup() {
    handle[MigrationHandler]()
    handle[ImageSetupHandler]()
    handle[CactaceaQueueHandler]()
  }

}
