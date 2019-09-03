package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MediumType}
import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId
import io.github.cactacea.backend.core.infrastructure.models.Mediums

case class Medium(
                   id: MediumId,
                   uri: String,
                   width: Long,
                   height: Long,
                   size: Long,
                   thumbnailUrl: Option[String],
                   mediumType: MediumType,
                   warning: Boolean,
                   rejected: Boolean
                  )

object Medium {

  def apply(i: Mediums): Medium = {
    i.contentStatus match {
      case ContentStatusType.rejected =>
        Medium(
          id              = i.id,
          uri             = "",
          width           = 0L,
          height          = 0L,
          size            = 0L,
          thumbnailUrl    = None,
          mediumType      = i.mediumType,
          warning         = false,
          rejected        = true
        )
      case _ =>
        Medium(
          id              = i.id,
          uri             = i.uri,
          width           = i.width,
          height          = i.height,
          size            = i.size,
          thumbnailUrl    = i.thumbnailUrl,
          mediumType      = i.mediumType,
          warning         = i.contentWarning,
          rejected        = false
        )
    }
  }

}
