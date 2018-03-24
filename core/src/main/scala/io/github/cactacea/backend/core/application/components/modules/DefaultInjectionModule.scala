package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.InjectionService
import io.github.cactacea.backend.core.application.components.services.DefaultInjectionService

object DefaultInjectionModule extends TwitterModule {

  override def configure() {
    bindSingleton[InjectionService].to[DefaultInjectionService]
  }

}
