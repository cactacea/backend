package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.Account
import io.github.cactacea.core.infrastructure.models.{AccountGroups, Accounts, Relationships}

object AccountFactory {

  def create(u: Accounts, r: Option[Relationships]): Account = {
    create(u, r, None)
  }

  def create(a: Accounts, r: Option[Relationships], ag: AccountGroups): Account = {
    create(a, r, Some(ag))
  }

  def create(a: Accounts): Account = {
    create(a, None, None)
  }

  def create(a: Accounts, r: Option[Relationships], ag: Option[AccountGroups]): Account = {
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
