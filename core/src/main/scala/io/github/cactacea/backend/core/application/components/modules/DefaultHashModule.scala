package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.HashService
import io.github.cactacea.backend.core.application.components.services.DefaultHashService

object DefaultHashModule extends TwitterModule {

  override def configure(): Unit = {
    bindSingleton[HashService].to[DefaultHashService]
  }
}
