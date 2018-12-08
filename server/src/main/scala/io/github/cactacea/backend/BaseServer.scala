package io.github.cactacea.backend

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.modules.{DefaultJacksonModule, _}
import io.github.cactacea.backend.utils.mappers.{CactaceaExceptionMapper, CaseClassExceptionMapper}

trait BaseServer extends HttpServer {

  protected  def databaseModule: TwitterModule = DatabaseModule

  override def jacksonModule: TwitterModule = DefaultJacksonModule

  def injectionModule: TwitterModule =  DefaultInjectionModule
  def fanOutModule: TwitterModule = DefaultNotificationModule
  def notificationMessagesModule: TwitterModule = DefaultNotificationMessagesModule
  def publishModule: TwitterModule =  DefaultEnqueueModule
  def pushNotificationModule: TwitterModule = DefaultPushNotificationModule
  def storageModule: TwitterModule = DefaultStorageModule
  def subScribeModule: TwitterModule = DefaultDequeueModule
  def deepLinkModule: TwitterModule = DefaultDeepLinkModule
  def transcodeModule: TwitterModule = DefaultTranscodeModule
  def hashModule: TwitterModule = DefaultHashModule

  addFrameworkModule(injectionModule)
  addFrameworkModule(fanOutModule)
  addFrameworkModule(notificationMessagesModule)
  addFrameworkModule(publishModule)
  addFrameworkModule(pushNotificationModule)
  addFrameworkModule(storageModule)
  addFrameworkModule(subScribeModule)
  addFrameworkModule(deepLinkModule)
  addFrameworkModule(transcodeModule)
  addFrameworkModule(databaseModule)
  addFrameworkModule(hashModule)

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .exceptionMapper[CaseClassExceptionMapper]
      .exceptionMapper[CactaceaExceptionMapper]
  }

}

