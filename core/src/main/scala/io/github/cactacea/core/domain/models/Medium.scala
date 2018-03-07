package io.github.cactacea.core.domain.models

import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.infrastructure.identifiers.MediumId
import io.github.cactacea.core.infrastructure.models.Mediums

case class Medium(
                   id: MediumId,
                   uri: String,
                   width: Long,
                   height: Long,
                   size: Long,
                   thumbnailUrl: Option[String],
                   mediumType: MediumType,
                   contentWarning: Boolean
                  )

object Medium {

  def apply(i: Mediums): Medium = {
    Medium(
      id              = i.id,
      uri             = i.uri,
      width           = i.width,
      height          = i.height,
      size            = i.size,
      thumbnailUrl    = i.thumbnailUri,
      mediumType      = i.mediumType,
      contentWarning  = i.contentWarning
    )
  }

}