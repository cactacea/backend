package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.{TimelineFeedId}

case class TimelineFeed (
                          id: TimelineFeedId,
                          feed: Option[Feed],
                          postedAt: Long
                    )
