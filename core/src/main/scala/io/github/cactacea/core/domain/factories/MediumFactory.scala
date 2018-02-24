package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.Medium
import io.github.cactacea.core.infrastructure.models.Mediums

object MediumFactory {

  def create(i: Mediums) = {
    Medium(
      id            = i.id,
      uri           = i.uri,
      width         = i.width,
      height        = i.height,
      size          = i.size,
      thumbnailUrl  = i.thumbnailUri,
      mediumType    = i.mediumType
    )
  }
}
