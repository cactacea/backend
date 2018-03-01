package io.github.cactacea.core.application.components.services

import com.google.inject.Singleton
import com.twitter.util.Future
import io.github.cactacea.core.application.components.interfaces.TranscodeService
import io.github.cactacea.core.infrastructure.identifiers.MediumId

@Singleton
class DefaultTranscodeService extends TranscodeService {

  def translate(mediumId: MediumId): Future[Unit] = {
    Future.Unit
  }

}
