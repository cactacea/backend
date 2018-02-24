package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{TimelineFeedId, FeedId, AccountId}

case class Timelines(
                          id: TimelineFeedId,
                          accountId: AccountId,
                          by: Option[AccountId],
                          feedId: Option[FeedId],
                          postedAt: Long
                    )
