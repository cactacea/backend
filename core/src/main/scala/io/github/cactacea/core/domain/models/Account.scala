package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.AccountId
import io.github.cactacea.core.infrastructure.models.{AccountGroups, Accounts, Relationships}
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

object Account {

  def apply(u: Accounts, r: Option[Relationships]): Account = {
    apply(u, r, None)
  }

  def apply(a: Accounts, r: Option[Relationships], ag: AccountGroups): Account = {
    apply(a, r, Some(ag))
  }

  def apply(a: Accounts): Account = {
    apply(a, None, None)
  }

  def apply(a: Accounts, r: Option[Relationships], ag: Option[AccountGroups]): Account = {
    Account(
      a.id,
      a.accountName,
      r.flatMap(_.editedDisplayName).getOrElse(a.displayName),
      a.profileImageUrl,
      r.map(_.friend).getOrElse(false),
      r.map(_.inProgress).getOrElse(false),
      r.map(_.followed).getOrElse(false),
      r.map(_.follower).getOrElse(false),
      Some(a.followCount),
      Some(a.followerCount),
      Some(a.friendCount),
      r.map(_.muted).getOrElse(false),
      a.web,
      a.birthday,
      a.location,
      a.bio,
      ag.map(_.joinedAt),
      a.position
    )
  }  
}