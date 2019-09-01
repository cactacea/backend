package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MediumType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, MediumId}

case class Mediums(
                    id: MediumId,
                    key: String,
                    uri: String,
                    width: Int,
                    height: Int,
                    size: Long,
                    thumbnailUrl: Option[String],
                    mediumType: MediumType,
                    by: UserId,
                    contentWarning: Boolean,
                    contentStatus: ContentStatusType
                  )
