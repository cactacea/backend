package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.AccountId
import org.joda.time.DateTime

case class Account(id: AccountId,
                   accountName: String,
                   displayName: String,
                   profileImageUrl: Option[String],
                   friend: Boolean,
                   friendRequestInProgress: Boolean,
                   followed: Boolean,
                   follower: Boolean,
                   followCount: Option[Long],
                   followerCount: Option[Long],
                   friendCount: Option[Long],
                   muted: Boolean,
                   web: Option[String],
                   birthday: Option[DateTime],
                   location: Option[String],
                   bio: Option[String],
                   joinedAt: Option[Long],
                   next: Long
                 )
