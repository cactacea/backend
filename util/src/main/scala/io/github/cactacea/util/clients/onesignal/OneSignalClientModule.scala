package io.github.cactacea.util.clients.onesignal

import com.google.inject.{Provides, Singleton}
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.TwitterModule

object OneSignalClientModule extends TwitterModule {

  override val modules = Seq(OneSignalHttpClientModule)

  @Singleton
  @Provides
  def provide(oneSignalHttpClient: HttpClient, finatraObjectMapper: FinatraObjectMapper): OneSignalClient = {
    new OneSignalClient(oneSignalHttpClient, finatraObjectMapper)
  }

}
