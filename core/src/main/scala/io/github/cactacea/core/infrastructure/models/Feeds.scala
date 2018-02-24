package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{FeedId, AccountId}

case class Feeds(
                  id: FeedId,
                  message: String,
                  privacyType: Long,
                  favoriteCount: Long,
                  commentCount: Long,
                  by: AccountId,
                  contentWarning: Boolean,
                  notified: Boolean,
                  delivered: Boolean,
                  postedAt: Long
                  )
