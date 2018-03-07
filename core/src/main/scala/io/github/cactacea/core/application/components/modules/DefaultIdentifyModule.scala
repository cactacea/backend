package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.IdentifyService
import io.github.cactacea.core.application.components.services.DefaultIdentifyService

object DefaultIdentifyModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[IdentifyService].to[DefaultIdentifyService]
  }

}
