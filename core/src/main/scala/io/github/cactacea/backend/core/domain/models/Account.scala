package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.infrastructure.models._

case class Account(id: AccountId,
                   accountName: String,
                   displayName: String,
                   profileImageUrl: Option[String],
                   isFriend: Boolean,
                   friendRequestInProgress: Boolean,
                   following: Boolean,
                   isFollower: Boolean,
                   followingCount: Option[Long],
                   followerCount: Option[Long],
                   friendCount: Option[Long],
                   feedsCount: Option[Long],
                   muting: Boolean,
                   blocking: Boolean,
                   web: Option[String],
                   birthday: Option[Long],
                   location: Option[String],
                   bio: Option[String],
                   joinedAt: Option[Long],
                   next: Option[Long]
                 )

object Account {

  def apply(a: Accounts, r: Option[Relationships], b: Blocks, n: Long): Account = {
    apply(a, r, None, Some(n), Some(b))
  }

  def apply(a: Accounts, r: Option[Relationships], n: Long): Account = {
    apply(a, r, None, Some(n), None)
  }

  def apply(a: Accounts, r: Option[Relationships]): Account = {
    apply(a, r, None, Some(a.id.value), None)
  }

  def apply(a: Accounts): Account = {
    apply(a, None, None, None, None)
  }

  private def apply(a: Accounts, r: Option[Relationships], ag: Option[AccountGroups], next: Option[Long], b: Option[Blocks]): Account = {
    Account(
      a.id,
      a.accountName,
      r.flatMap(_.displayName).getOrElse(a.displayName),
      a.profileImageUrl,
      r.map(_.isFriend).getOrElse(false),
      r.map(_.friendRequestInProgress).getOrElse(false),
      r.map(_.following).getOrElse(false),
      r.map(_.isFollower).getOrElse(false),
      Some(a.followCount),
      Some(a.followerCount),
      Some(a.friendCount),
      Some(a.feedsCount),
      r.map(_.muting).getOrElse(false),
      b.map(_ => true).getOrElse(false),
      a.web,
      a.birthday,
      a.location,
      a.bio,
      ag.map(_.joinedAt),
      next
    )
  }

}
