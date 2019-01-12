package io.github.cactacea.backend

class APIServer extends CactaceaServer {

  override def warmup() {
    handle[APISetupHandler]()
  }

}
