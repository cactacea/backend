package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.UserStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.UserId
import io.github.cactacea.backend.core.infrastructure.models.{Blocks, Relationships, UserChannels, Users}

case class User(id: UserId,
                userName: String,
                displayName: String,
                profileImageUrl: Option[String],
                isFriend: Boolean,
                friendRequestInProgress: Boolean,
                follow: Boolean,
                isFollower: Boolean,
                followCount: Long,
                followerCount: Long,
                friendCount: Long,
                tweetCount: Long,
                muted: Boolean,
                blocked: Boolean,
                web: Option[String],
                birthday: Option[Long],
                location: Option[String],
                bio: Option[String],
                joinedAt: Option[Long],
                userStatus: UserStatusType,
                signedOutAt: Option[Long],
                next: Long
                        )


object User {

  def apply(a: Users, r: Option[Relationships], b: Blocks): User = {
    apply(a, r, None, b.id.value, Some(b))
  }

  def apply(a: Users, r: Option[Relationships], n: Long): User = {
    apply(a, r, None, n, None)
  }

  def apply(a: Users, r: Option[Relationships]): User = {
    apply(a, r, None, a.id.value, None)
  }

  def apply(a: Users): User = {
    apply(a, None, None, a.id.value, None)
  }

  private def apply(a: Users, r: Option[Relationships], ag: Option[UserChannels], next: Long, b: Option[Blocks]): User = {
    User(
      a.id,
      a.userName,
      r.flatMap(_.displayName).getOrElse(a.displayName),
      a.profileImageUrl,
      r.map(_.isFriend).getOrElse(false),
      r.map(_.friendRequestInProgress).getOrElse(false),
      r.map(_.follow).getOrElse(false),
      r.map(_.isFollower).getOrElse(false),
      a.followCount - r.map(_.followBlockCount).getOrElse(0L),
      a.followerCount - r.map(_.followerBlockCount).getOrElse(0L),
      a.friendCount - r.map(_.friendBlockCount).getOrElse(0L),
      a.tweetCount,
      r.map(_.muting).getOrElse(false),
      b.map(_ => true).getOrElse(false),
      a.web,
      a.birthday,
      a.location,
      a.bio,
      ag.map(_.joinedAt),
      a.userStatus,
      a.signedOutAt,
      next
    )
  }

}