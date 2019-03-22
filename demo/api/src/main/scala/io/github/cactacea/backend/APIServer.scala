package io.github.cactacea.backend

import com.twitter.finatra.http.routing.HttpRouter
import io.cactacea.finagger.DocsController
import io.github.cactacea.backend.swagger.CactaceaSwaggerModule
import io.github.cactacea.backend.utils.warmups.QueueHandler

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
    handle[QueueHandler]()
  }

}
