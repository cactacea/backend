package io.github.cactacea.backend.core.application.components

import com.twitter.inject.TwitterModule
import com.twitter.inject.app.App
import io.github.cactacea.backend.core.application.components.modules._

trait BaseModules extends App {

  protected  def databaseModule: TwitterModule = DatabaseModule

  def injectionModule: TwitterModule =  DefaultListenerModule
  def chatFanOutModule: TwitterModule = DefaultChatModule
  def notificationMessagesModule: TwitterModule = DefaultMessageModule
  def enqueueModule: TwitterModule =  DefaultQueueModule
  def pushNotificationModule: TwitterModule = DefaultMobilePushModule
  def storageModule: TwitterModule = DefaultStorageModule
  def deepLinkModule: TwitterModule = DefaultDeepLinkModule
  def transcodeModule: TwitterModule = DefaultTranscodeModule
  def hashModule: TwitterModule = DefaultHashModule

  addFrameworkModule(injectionModule)
  addFrameworkModule(chatFanOutModule)
  addFrameworkModule(notificationMessagesModule)
  addFrameworkModule(enqueueModule)
  addFrameworkModule(pushNotificationModule)
  addFrameworkModule(storageModule)
  addFrameworkModule(deepLinkModule)
  addFrameworkModule(transcodeModule)
  addFrameworkModule(databaseModule)
  addFrameworkModule(hashModule)

}
