package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.SubScribeService
import io.github.cactacea.backend.core.application.components.services.DefaultSubScribeService

object DefaultSubScribeModule extends TwitterModule {

  override def configure() {
    bindSingleton[SubScribeService].to[DefaultSubScribeService]
  }

}
