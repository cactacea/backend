package io.github.cactacea.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.core.infrastructure.identifiers.MediumId

trait TranscodeService {
  def translate(mediumId: MediumId): Future[Unit]
}
