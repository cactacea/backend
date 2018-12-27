package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.github.cactacea.backend.core.infrastructure.models._

case class Group(
                  id: GroupId,
                  name: Option[String],
                  message: Option[Message],
                  invitationOnly: Boolean,
                  privacyType: GroupPrivacyType,
                  authorityType: GroupAuthorityType,
                  accountCount: Long,
                  lastPostedAt: Option[Long],
                  organizedAt: Long,
                  next: Option[Long]
                 )

object Group {

  def apply(g: Groups): Group = {
    Group(
      id                = g.id,
      name              = g.name,
      message           = None,
      invitationOnly    = g.invitationOnly,
      privacyType       = g.privacyType,
      authorityType     = g.authorityType,
      accountCount      = g.accountCount,
      organizedAt       = g.organizedAt,
      lastPostedAt      = g.lastPostedAt,
      next              = None
    )
  }

  def apply(g: Groups, n: Long): Group = {
    Group(
      id                = g.id,
      name              = g.name,
      message           = None,
      invitationOnly    = g.invitationOnly,
      privacyType       = g.privacyType,
      authorityType     = g.authorityType,
      accountCount      = g.accountCount,
      organizedAt       = g.organizedAt,
      lastPostedAt      = g.lastPostedAt,
      next              = Some(n)
    )
  }

  def apply(g: Groups, am: AccountMessages, m: Messages, i: Option[Mediums], a: Accounts, r: Option[Relationships], n: Long): Group = {
    val message = Message(m, am, i, a, r, None)
    Group(
      id                = g.id,
      name              = g.name,
      message           = Some(message),
      invitationOnly    = g.invitationOnly,
      privacyType       = g.privacyType,
      authorityType     = g.authorityType,
      accountCount      = g.accountCount,
      organizedAt       = g.organizedAt,
      lastPostedAt      = g.lastPostedAt,
      next              = Some(n)
    )
  }

}
