package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.MessageService
import io.github.cactacea.backend.core.application.components.services.DefaultMessageService

object DefaultMessageModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[MessageService].to[DefaultMessageService]
  }

}
