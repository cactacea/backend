package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{FeedId}

case class FeedTags(
                     feedId: FeedId,
                     name: String,
                     registerAt: Long
                   )
