package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MediumType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MediumId}

case class Mediums(
                    id: MediumId,
                    key: String,
                    uri: String,
                    width: Int,
                    height: Int,
                    size: Long,
                    thumbnailUri: Option[String],
                    mediumType: MediumType,
                    by: AccountId,
                    contentWarning: Boolean,
                    contentStatus: ContentStatusType
                  )
