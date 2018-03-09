package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.domain.enums.{ContentStatusType, FeedPrivacyType}
import io.github.cactacea.core.infrastructure.identifiers.{AccountId, FeedId}

case class Feeds(
                  id: FeedId,
                  message: String,
                  privacyType: FeedPrivacyType,
                  likeCount: Long,
                  commentCount: Long,
                  by: AccountId,
                  contentWarning: Boolean,
                  contentStatus: ContentStatusType,
                  expiration: Option[Long],
                  notified: Boolean,
                  postedAt: Long
                  )
