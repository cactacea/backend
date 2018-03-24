package io.github.cactacea.backend.core.infrastructure.results

import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId

case class FeedBlocksCount(
                            id: FeedId,
                            count: Long
                          )