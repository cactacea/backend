package io.github.cactacea.backend.addons

import com.twitter.inject.TwitterModule
import io.github.cactacea.addons.aws.storage.AWSS3StorageModule
import io.github.cactacea.addons.onesignal.mobilepush.OneSignalMobilePushModule
import io.github.cactacea.addons.redis.chat.RedisChatModule
import io.github.cactacea.backend.CactaceaServer
import io.github.cactacea.backend.utils.warmups.QueueHandler

class ExampleServer extends CactaceaServer {

  override def chatModule: TwitterModule = RedisChatModule
  override def storageModule: TwitterModule = AWSS3StorageModule
  override def mobilePushModule: TwitterModule = OneSignalMobilePushModule

  override def warmup() {
    handle[ExampleMigrationHandler]()
    handle[QueueHandler]()
  }

}
