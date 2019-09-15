package io.github.cactacea.backend.core.infrastructure.models

import io.github.cactacea.backend.core.domain.enums.{FeedType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{FeedId, UserId}

case class Feeds(
                  id: FeedId,
                  userId: UserId,
                  by: UserId,
                  feedType: FeedType,
                  contentId: Option[Long],
                  url: String,
                  unread: Boolean,
                  notifiedAt: Long
                         )
