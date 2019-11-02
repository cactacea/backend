package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{InformationType}
import io.github.cactacea.backend.core.infrastructure.identifiers.InformationId
import io.github.cactacea.backend.core.infrastructure.models.Informations

case class Information(
                 id: InformationId,
                 informationType: InformationType,
                 contentId: Option[Long],
                 message: String,
                 url: String,
                 notifiedAt: Long,
                 next: Long
                         )

object Information {

  def apply(n: Informations, message: String, nextId: Long): Information = {
    new Information(
      n.id,
      n.informationType,
      n.contentId,
      message,
      n.url,
      n.notifiedAt,
      nextId)
  }

}
