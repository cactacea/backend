package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.ListenerService
import io.github.cactacea.backend.core.application.components.services.DefaultListenerService

object DefaultListenerModule extends TwitterModule {

  override def configure() {
    bindSingleton[ListenerService].to[DefaultListenerService]
  }

}
