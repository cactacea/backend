package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, TweetPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, TweetId, MediumId}

case class Tweets(
                   id: TweetId,
                   message: String,
                   tags: Option[String],
                   mediumId1: Option[MediumId],
                   mediumId2: Option[MediumId],
                   mediumId3: Option[MediumId],
                   mediumId4: Option[MediumId],
                   mediumId5: Option[MediumId],
                   privacyType: TweetPrivacyType,
                   likeCount: Long,
                   commentCount: Long,
                   by: UserId,
                   contentWarning: Boolean,
                   contentStatus: ContentStatusType,
                   expiration: Option[Long],
                   notified: Boolean,
                   postedAt: Long
                  )
