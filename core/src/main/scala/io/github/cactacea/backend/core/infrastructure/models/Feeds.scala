package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, FeedPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, FeedId, MediumId}

case class Feeds(
                  id: FeedId,
                  message: String,
                  tags: Option[String],
                  mediumId1: Option[MediumId],
                  mediumId2: Option[MediumId],
                  mediumId3: Option[MediumId],
                  mediumId4: Option[MediumId],
                  mediumId5: Option[MediumId],
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
