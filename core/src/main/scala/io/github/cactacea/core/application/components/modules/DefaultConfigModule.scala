package io.github.cactacea.core.application.components.modules

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import com.twitter.inject.annotations.Flag
import io.github.cactacea.core.application.components.services.DefaultConfigService

object DefaultConfigModule extends TwitterModule {

  flag(name = "tokenExpire", default = "864000", help = "auth token expire")
  flag(name = "tokenSubject", default = "Cactacea", help = "auth token expire")
  flag(name = "tokenIssuer", default = "Cactacea", help = "auth token expire")
  flag(name = "maxGroupCount", default = "0", help = "auth token expire")
  flag(name = "maxGroupAccountCount", default = "0", help = "auth token expire")

  @Provides
  @Singleton
  def provideConfig(@Flag("tokenExpire") tokenExpire: Long,
                    @Flag("tokenSubject") tokenSubject: String,
                    @Flag("tokenIssuer") tokenIssuer: String,
                    @Flag("maxGroupCount") maxGroupCount: Long,
                    @Flag("maxGroupAccountCount") maxGroupAccountCount: Long): DefaultConfigService = {

    DefaultConfigService(tokenExpire, tokenSubject, tokenIssuer, maxGroupCount, maxGroupAccountCount)
  }

}
