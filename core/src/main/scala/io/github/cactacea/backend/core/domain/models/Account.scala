package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId
import io.github.cactacea.backend.core.infrastructure.models._

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
                   feedsCount: Option[Long],
                   mute: Boolean,
                   web: Option[String],
                   birthday: Option[Long],
                   location: Option[String],
                   bio: Option[String],
                   joinedAt: Option[Long],
                   next: Long
                 )

object Account {

  def apply(a: Accounts, r: Option[Relationships], f: Followers): Account = {
    apply(a, r, None, f.followedAt)
  }

  def apply(a: Accounts, r: Option[Relationships], f: Friends): Account = {
    apply(a, r, None, f.friendedAt)
  }

  def apply(a: Accounts, r: Option[Relationships], f: Follows): Account = {
    apply(a, r, None, f.followedAt)
  }

  def apply(a: Accounts, r: Option[Relationships], b: Blocks): Account = {
    apply(a, r, None, b.blockedAt)
  }

  def apply(a: Accounts, r: Option[Relationships], m: Mutes): Account = {
    apply(a, r, None, m.mutedAt)
  }

  def apply(a: Accounts, r: Option[Relationships], ag: AccountGroups): Account = {
    apply(a, r, Some(ag), ag.joinedAt)
  }

  def apply(a: Accounts, r: Option[Relationships], fl: FeedLikes): Account = {
    apply(a, r, None, fl.likedAt)
  }

  def apply(a: Accounts, r: Option[Relationships], c: CommentLikes): Account = {
    apply(a, r, None, c.likedAt)
  }

  def apply(a: Accounts, r: Option[Relationships]): Account = {
    apply(a, r, None, a.id.value)
  }

  def apply(a: Accounts): Account = {
    apply(a, None, None, a.id.value)
  }

  private def apply(a: Accounts, r: Option[Relationships], ag: Option[AccountGroups], next: Long): Account = {
    Account(
      a.id,
      a.accountName,
      r.flatMap(_.displayName).getOrElse(a.displayName),
      a.profileImageUrl,
      r.map(_.friend).getOrElse(false),
      r.map(_.inProgress).getOrElse(false),
      r.map(_.follow).getOrElse(false),
      r.map(_.follower).getOrElse(false),
      Some(a.followCount),
      Some(a.followerCount),
      Some(a.friendCount),
      Some(a.feedsCount),
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
