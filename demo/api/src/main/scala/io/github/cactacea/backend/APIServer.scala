package io.github.cactacea.backend

import io.github.cactacea.backend.utils.warmups.QueueHandler

class APIServer extends CactaceaServer {

  override def warmup() {
    handle[APISetupHandler]()
    handle[QueueHandler]()
  }

}
