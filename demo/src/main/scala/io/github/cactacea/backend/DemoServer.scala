package io.github.cactacea.backend

class DemoServer extends CactaceaServer {

  override def warmup() {
    handle[DemoSetupHandler]()
  }

}
