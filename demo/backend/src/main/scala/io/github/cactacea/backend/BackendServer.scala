package io.github.cactacea.backend

class BackendServer extends CactaceaServer {

  override def warmup() {
    handle[BackendSetupHandler]()
  }

}
