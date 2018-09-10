package io.github.cactacea.backend.externals.infrastructure.authentications

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.SocialAccountsService

object GeneralSocialAccountsModule extends TwitterModule {

  override def configure() = {
    bindSingleton[SocialAccountsService].to[GeneralSocialAccountsService]
  }
}
