package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{MediumId, FeedId}

case class FeedMediums(
                        feedId: FeedId,
                        mediumId: MediumId,
                        registerAt: Long
                       )
