package io.github.cactacea.util.clients.onesignal

import com.twitter.inject.TwitterModule

object OneSignalClientModule extends TwitterModule {

  override val modules = Seq(
    OneSignalHttpClientModule
  )

  override def configure(): Unit = {
    bindSingleton[OneSignalClient]
  }

  //  @Singleton
//  @Provides
//  def provide(oneSignalHttpClient: HttpClient, finatraObjectMapper: FinatraObjectMapper): OneSignalClient = {
//    new OneSignalClient(oneSignalHttpClient, finatraObjectMapper)
//  }

}

