package io.github.cactacea.backend.server.addons

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.addons.aws.storage.AWSS3StorageModule
import io.github.cactacea.backend.addons.onesignal.mobilepush.OneSignalMobilePushModule
import io.github.cactacea.backend.addons.redis.chat.RedisChatModule
import io.github.cactacea.backend.server.CactaceaServer
import io.github.cactacea.backend.server.utils.warmups.CactaceaQueueHandler

class ExampleServer extends CactaceaServer {

  override def chatModule: TwitterModule = RedisChatModule
  override def storageModule: TwitterModule = AWSS3StorageModule
  override def mobilePushModule: TwitterModule = OneSignalMobilePushModule

  override def warmup() {
    handle[ExampleMigrationHandler]()
    handle[CactaceaQueueHandler]()
  }

}
