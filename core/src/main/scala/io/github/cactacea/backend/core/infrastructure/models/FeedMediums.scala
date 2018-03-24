package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{MediumId, FeedId}

case class FeedMediums(
                        feedId: FeedId,
                        mediumId: MediumId,
                        orderNo: Long
                       )
