package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId

trait TranscodeService {
  def translate(mediumId: MediumId): Future[Unit]
}
