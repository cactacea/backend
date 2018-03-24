package io.github.cactacea.backend.core.application.components.modules

import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.interfaces.TranscodeService
import io.github.cactacea.backend.core.application.components.services.DefaultTranscodeService

object DefaultTranscodeModule extends TwitterModule {

  override def configure() {
    bindSingleton[TranscodeService].to[DefaultTranscodeService]
  }

}
