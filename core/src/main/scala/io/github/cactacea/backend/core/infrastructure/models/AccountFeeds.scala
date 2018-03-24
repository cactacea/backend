package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId}

case class AccountFeeds (
                        accountId: AccountId,
                        feedId: FeedId,
                        by: AccountId,
                        notified: Boolean,
                        postedAt: Long
                        )
