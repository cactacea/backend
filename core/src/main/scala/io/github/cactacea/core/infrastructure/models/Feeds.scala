package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId}

case class Feeds(
                  id: FeedId,
                  message: String,
                  privacyType: FeedPrivacyType,
                  favoriteCount: Long,
                  commentCount: Long,
                  by: AccountId,
                  contentWarning: Boolean,
                  notified: Boolean,
                  postedAt: Long
                  )
