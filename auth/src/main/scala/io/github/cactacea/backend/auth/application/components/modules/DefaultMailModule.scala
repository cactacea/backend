package io.github.cactacea.backend.auth.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.auth.application.components.interfaces.MailService
import io.github.cactacea.backend.auth.application.components.services.DefaultMailService

object DefaultMailModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[MailService].to[DefaultMailService]
  }

}
