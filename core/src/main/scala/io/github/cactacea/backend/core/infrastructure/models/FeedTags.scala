package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId}

case class FeedTags(
                     feedId: FeedId,
                     name: String,
                     orderNo: Long
                   )
