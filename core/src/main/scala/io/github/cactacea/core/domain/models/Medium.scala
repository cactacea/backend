package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.MediumId

case class Medium(
                   id: MediumId,
                   uri: String,
                   width: Long,
                   height: Long,
                   size: Long,
                   thumbnailUrl: Option[String],
                   mediumType: Long
                  )
