package io.github.cactacea.backend.core.application.components

import com.twitter.inject.TwitterModule
import com.twitter.inject.app.App
import io.github.cactacea.backend.core.application.components.modules._

trait CactaceaApp extends App {

  def databaseModule: TwitterModule = DatabaseModule
  def chatModule: TwitterModule = DefaultChatModule
  def messageModule: TwitterModule = DefaultMessageModule
  def queueModule: TwitterModule =  DefaultQueueModule
  def mobilePushModule: TwitterModule = DefaultMobilePushModule
  def storageModule: TwitterModule = DefaultStorageModule
  def deepLinkModule: TwitterModule = DefaultDeepLinkModule

  addFrameworkModule(chatModule)
  addFrameworkModule(messageModule)
  addFrameworkModule(queueModule)
  addFrameworkModule(mobilePushModule)
  addFrameworkModule(storageModule)
  addFrameworkModule(deepLinkModule)
  addFrameworkModule(databaseModule)

}
