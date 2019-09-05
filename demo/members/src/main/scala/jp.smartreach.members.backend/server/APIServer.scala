package io.github.cactacea.backend.server

import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.addons.aws.storage.AWSS3StorageModule
import io.github.cactacea.backend.addons.onesignal.mobilepush.OneSignalMobilePushModule
import io.github.cactacea.backend.addons.redis.chat.RedisChatModule
import io.github.cactacea.backend.server.utils.swagger.CactaceaSwaggerModule
import io.github.cactacea.backend.server.utils.warmups.CactaceaQueueHandler
import io.github.cactacea.finagger.DocsController

class APIServer extends CactaceaServer {

  override def chatModule: TwitterModule = RedisChatModule
  override def storageModule: TwitterModule = AWSS3StorageModule
  override def mobilePushModule: TwitterModule = OneSignalMobilePushModule

  override def configureHttp(router: HttpRouter): Unit = {
    super.configureHttp(router)
    router
      .add[DocsController]
  }

  addFrameworkModule(CactaceaSwaggerModule)

  override def warmup() {
    handle[MigrationHandler]()
    handle[CactaceaQueueHandler]()
  }

}
