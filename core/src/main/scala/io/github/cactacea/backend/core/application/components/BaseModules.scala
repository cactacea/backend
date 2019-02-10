package io.github.cactacea.backend.core.application.components

import com.twitter.inject.TwitterModule
import com.twitter.inject.app.App
import io.github.cactacea.backend.core.application.components.modules._

trait BaseModules extends App {

  protected  def databaseModule: TwitterModule = DatabaseModule

  def listenerModule: TwitterModule =  DefaultListenerModule
  def chatModule: TwitterModule = DefaultChatModule
  def messageModule: TwitterModule = DefaultMessageModule
  def queueModule: TwitterModule =  DefaultQueueModule
  def mobilePushModule: TwitterModule = DefaultMobilePushModule
  def storageModule: TwitterModule = DefaultStorageModule
  def deepLinkModule: TwitterModule = DefaultDeepLinkModule
  def hashModule: TwitterModule = DefaultHashModule

  addFrameworkModule(listenerModule)
  addFrameworkModule(chatModule)
  addFrameworkModule(messageModule)
  addFrameworkModule(queueModule)
  addFrameworkModule(mobilePushModule)
  addFrameworkModule(storageModule)
  addFrameworkModule(deepLinkModule)
  addFrameworkModule(databaseModule)
  addFrameworkModule(hashModule)

}
