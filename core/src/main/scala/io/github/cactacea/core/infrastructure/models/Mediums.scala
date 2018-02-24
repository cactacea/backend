package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{MediumId, AccountId}

case class Mediums(
                  id: MediumId,
                  key: String,
                  uri: String,
                  width: Int,
                  height: Int,
                  size: Long,
                  thumbnailUri: Option[String],
                  mediumType: Long,
                  by: AccountId
                  )
