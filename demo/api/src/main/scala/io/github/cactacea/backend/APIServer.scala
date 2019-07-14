package io.github.cactacea.backend

import com.twitter.finatra.http.routing.HttpRouter
import io.cactacea.finagger.DocsController
import io.github.cactacea.backend.utils.auth.CactaceaAuthModule
import io.github.cactacea.backend.utils.swagger.CactaceaSwaggerModule
import io.github.cactacea.backend.utils.warmups.CactaceaQueueHandler

class APIServer extends CactaceaServer {

  override def configureHttp(router: HttpRouter): Unit = {
    super.configureHttp(router)
    router
      .add[DocsController]
  }

  addFrameworkModule(CactaceaSwaggerModule)
  addFrameworkModule(CactaceaAuthModule)

  override def warmup() {
    handle[MigrationHandler]()
    handle[ImageSetupHandler]()
    handle[CactaceaQueueHandler]()
  }

}
