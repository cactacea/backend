package io.github.cactacea.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.core.application.components.interfaces.ActionService
import io.github.cactacea.core.application.components.services.DefaultActionService

object DefaultActionModule extends TwitterModule {

  override def configure() {
    bindSingleton[ActionService].to(classOf[DefaultActionService])
  }

}
