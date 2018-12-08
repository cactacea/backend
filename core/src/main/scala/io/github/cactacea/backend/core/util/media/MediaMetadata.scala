package io.github.cactacea.backend.core.util.media

import io.github.cactacea.backend.core.domain.enums.MediumType

case class MediaMetadata(
                          mediumType: MediumType,
                          width: Int,
                          height: Int,
                          data: Array[Byte],
                          contentType: Option[String]
                        )
