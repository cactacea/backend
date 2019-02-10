package io.github.cactacea.backend

import io.github.cactacea.backend.utils.warmups.QueueHandler

class APIServer extends CactaceaServer {

  override def warmup() {
    handle[MigrationHandler]()
    handle[ImageSetupHandler]()
    handle[QueueHandler]()
  }

}
