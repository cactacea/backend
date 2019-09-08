package io.github.cactacea.backend.server.utils.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.server.utils.filters.CactaceaAuthenticationFilterFactory

object DefaultAuthFilterModule extends TwitterModule {

  override protected def configure(): Unit = {
    bindAssistedFactory[CactaceaAuthenticationFilterFactory]()
  }

}