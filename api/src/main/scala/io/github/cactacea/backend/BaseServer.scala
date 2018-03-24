package io.github.cactacea.backend

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.util.mappers.{CactaceaExceptionMapper, CaseClassExceptionMapper}
import io.github.cactacea.core.application.components.modules._
import io.github.cactacea.core.util.warmups.DatabaseMigrationHandler

class BaseServer extends HttpServer {

  override val disableAdminHttpServer = false
  override val defaultFinatraHttpPort = ":9000"
  override val defaultAdminPort = 9001
  override val defaultHttpServerName = "Backend Server"

  protected  def databaseModule = DatabaseProviderModule

  def identifyModule: TwitterModule =  DefaultIdentifyModule
  def socialAccountsModule: TwitterModule = FGTSocialAccountsModule
  def injectionModule: TwitterModule =  NoActionInjectionModule
  def configModule: TwitterModule = DefaultConfigModule
  def fanOutModule: TwitterModule = DefaultNotificationModule
  def notificationMessagesModule: TwitterModule = DefaultNotificationMessagesModule
  def publishModule: TwitterModule =  NoQueuePublishModule
  def pushNotificationModule: TwitterModule = NoPushNotificationModule
  def storageModule: TwitterModule = LocalStorageModule
  def subScribeModule: TwitterModule = NoQueueSubScribeModule
  def deepLinkModule: TwitterModule = DefaultDeepLinkModule
  def transcodeModule: TwitterModule = ImageTranscodeModule

  addFrameworkModule(identifyModule)
  addFrameworkModule(socialAccountsModule)
  addFrameworkModule(injectionModule)
  addFrameworkModule(configModule)
  addFrameworkModule(fanOutModule)
  addFrameworkModule(notificationMessagesModule)
  addFrameworkModule(publishModule)
  addFrameworkModule(pushNotificationModule)
  addFrameworkModule(storageModule)
  addFrameworkModule(subScribeModule)
  addFrameworkModule(deepLinkModule)
  addFrameworkModule(transcodeModule)
  addFrameworkModule(databaseModule)

  override def configureHttp(router: HttpRouter) = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .exceptionMapper[CaseClassExceptionMapper]
      .exceptionMapper[CactaceaExceptionMapper]
  }

  override def warmup() {
    handle[DatabaseMigrationHandler]()
  }

}
