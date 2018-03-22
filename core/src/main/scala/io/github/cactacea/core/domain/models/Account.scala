package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.AccountId
import io.github.cactacea.core.infrastructure.models._

case class Account(id: AccountId,
                   accountName: String,
                   displayName: String,
                   profileImageUrl: Option[String],
                   friend: Boolean,
                   friendRequestInProgress: Boolean,
                   follow: Boolean,
                   follower: Boolean,
                   followCount: Option[Long],
                   followerCount: Option[Long],
                   friendCount: Option[Long],
                   mute: Boolean,
                   web: Option[String],
                   birthday: Option[Long],
                   location: Option[String],
                   bio: Option[String],
                   joinedAt: Option[Long],
                   next: Long
                 )

object Account {

  def apply(a: Accounts, r: Option[Relationships], b: Blocks): Account = {
    apply(a, r, None, b.id.value)
  }

  def apply(a: Accounts, r: Option[Relationships], ag: AccountGroups): Account = {
    apply(a, r, Some(ag), ag.id.value)
  }

  def apply(a: Accounts, r: Option[Relationships], fl: FeedLikes): Account = {
    apply(a, r, None, fl.id.value)
  }

  def apply(a: Accounts, r: Option[Relationships], c: CommentLikes): Account = {
    apply(a, r, None, c.id.value)
  }

  def apply(a: Accounts, r: Option[Relationships]): Account = {
    apply(a, None, None, a.id.value)
  }

  def apply(a: Accounts): Account = {
    apply(a, None, None, a.id.value)
  }

  def apply(a: Accounts, r: Option[Relationships], next: Long): Account = {
    apply(a, None, None, next)
  }

  private def apply(a: Accounts, r: Option[Relationships], ag: Option[AccountGroups], next: Long): Account = {
    Account(
      a.id,
      a.accountName,
      r.flatMap(_.editedDisplayName).getOrElse(a.displayName),
      a.profileImageUrl,
      r.map(_.friend).getOrElse(false),
      r.map(_.inProgress).getOrElse(false),
      r.map(_.follow).getOrElse(false),
      r.map(_.follower).getOrElse(false),
      Some(a.followCount),
      Some(a.followerCount),
      Some(a.friendCount),
      r.map(_.mute).getOrElse(false),
      a.web,
      a.birthday,
      a.location,
      a.bio,
      ag.map(_.joinedAt),
      next
    )
  }  
}