package io.github.cactacea.core.infrastructure.queries

import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class FeedBlocksCountQuery(
                            id: FeedId,
                            count: Long
                          )