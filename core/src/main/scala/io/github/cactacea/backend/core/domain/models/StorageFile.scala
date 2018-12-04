package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.MediumType

case class StorageFile(
                key: String,
                url: String,
                width: Int,
                height: Int,
                length: Int,
                mediumType: MediumType
                )
