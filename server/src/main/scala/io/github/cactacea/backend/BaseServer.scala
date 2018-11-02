package io.github.cactacea.backend

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.modules._
import io.github.cactacea.backend.utils.mappers.{CactaceaExceptionMapper, CaseClassExceptionMapper}

trait BaseServer extends HttpServer {

  protected  def databaseModule = DatabaseProviderModule

  def injectionModule: TwitterModule =  DefaultInjectionModule
  def fanOutModule: TwitterModule = DefaultNotificationModule
  def notificationMessagesModule: TwitterModule = DefaultNotificationMessagesModule
  def publishModule: TwitterModule =  DefaultPublishModule
  def pushNotificationModule: TwitterModule = DefaultPushNotificationModule
  def storageModule: TwitterModule = DefaultStorageModule
  def subScribeModule: TwitterModule = DefaultSubScribeModule
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

  override def configureHttp(router: HttpRouter) = {
    router
      .exceptionMapper[CaseClassExceptionMapper]
      .exceptionMapper[CactaceaExceptionMapper]
  }

}

