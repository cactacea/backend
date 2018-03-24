package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.SocialAccountsService
import io.github.cactacea.backend.core.application.components.services.DefaultSocialAccountsService

object DefaultSocialAccountsModule extends TwitterModule {

  override def configure() = {
    bindSingleton[SocialAccountsService].to[DefaultSocialAccountsService]
  }

}
