package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.InjectionService
import io.github.cactacea.core.application.components.services.NoActionInjectionService

object NoActionInjectionModule extends TwitterModule {

  override def configure() {
    bindSingleton[InjectionService].to[NoActionInjectionService]
  }

}
