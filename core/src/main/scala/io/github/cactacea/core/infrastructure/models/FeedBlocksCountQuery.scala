package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.FeedId

case class FeedBlocksCountQuery(
                            id: FeedId,
                            count: Long
                          )