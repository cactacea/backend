package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.MediumType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, MediumId}

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
                    contentWarning: Boolean
                  )
