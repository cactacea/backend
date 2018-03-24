package io.github.cactacea.backend.components.authentications

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.SocialAccountsService

object GeneralSocialAccountsModule extends TwitterModule {

  override def configure() = {
    bindSingleton[SocialAccountsService].to[GeneralSocialAccountsService]
  }
}
