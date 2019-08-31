package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.AccountStatusType
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.infrastructure.models.{AccountChannels, Accounts, Blocks, Relationships}

case class Account(id: AccountId,
                   accountName: String,
                   displayName: String,
                   profileImageUrl: Option[String],
                   isFriend: Boolean,
                   friendRequestInProgress: Boolean,
                   follow: Boolean,
                   isFollower: Boolean,
                   followCount: Long,
                   followerCount: Long,
                   friendCount: Long,
                   feedCount: Long,
                   muted: Boolean,
                   blocked: Boolean,
                   web: Option[String],
                   birthday: Option[Long],
                   location: Option[String],
                   bio: Option[String],
                   joinedAt: Option[Long],
                   accountStatus: AccountStatusType,
                   signedOutAt: Option[Long],
                   next: Long
                        )


object Account {

  def apply(a: Accounts, r: Option[Relationships], b: Blocks): Account = {
    apply(a, r, None, b.id.value, Some(b))
  }

  def apply(a: Accounts, r: Option[Relationships], n: Long): Account = {
    apply(a, r, None, n, None)
  }

  def apply(a: Accounts, r: Option[Relationships]): Account = {
    apply(a, r, None, a.id.value, None)
  }

  def apply(a: Accounts): Account = {
    apply(a, None, None, a.id.value, None)
  }

  private def apply(a: Accounts, r: Option[Relationships], ag: Option[AccountChannels], next: Long, b: Option[Blocks]): Account = {
    Account(
      a.id,
      a.accountName,
      r.flatMap(_.displayName).getOrElse(a.displayName),
      a.profileImageUrl,
      r.map(_.isFriend).getOrElse(false),
      r.map(_.friendRequestInProgress).getOrElse(false),
      r.map(_.follow).getOrElse(false),
      r.map(_.isFollower).getOrElse(false),
      a.followCount - r.map(_.followBlockCount).getOrElse(0L),
      a.followerCount - r.map(_.followerBlockCount).getOrElse(0L),
      a.friendCount - r.map(_.friendBlockCount).getOrElse(0L),
      a.feedCount,
      r.map(_.muting).getOrElse(false),
      b.map(_ => true).getOrElse(false),
      a.web,
      a.birthday,
      a.location,
      a.bio,
      ag.map(_.joinedAt),
      a.accountStatus,
      a.signedOutAt,
      next
    )
  }

}