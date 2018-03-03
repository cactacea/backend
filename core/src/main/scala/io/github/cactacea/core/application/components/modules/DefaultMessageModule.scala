package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.MessageService
import io.github.cactacea.core.application.components.services.DefaultMessageService

object DefaultMessageModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[MessageService].to(classOf[DefaultMessageService])
  }

}
