package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.SocialAccountsService
import io.github.cactacea.core.application.components.services.DefaultSocialAccountsService
import io.github.cactacea.util.clients.facebook.FacebookClientModule
import io.github.cactacea.util.clients.google.GoogleClientModule
import io.github.cactacea.util.clients.twitter.TwitterClientModule

object DefaultSocialAccountsModule extends TwitterModule {

  override val modules = Seq(
    FacebookClientModule,
    GoogleClientModule,
    TwitterClientModule
  )

  override def configure() = {
    bindSingleton[SocialAccountsService].to[DefaultSocialAccountsService]
  }

}
