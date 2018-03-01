package io.github.cactacea.core.infrastructure.results

import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class FeedBlocksCount(
                            id: FeedId,
                            count: Long
                          )