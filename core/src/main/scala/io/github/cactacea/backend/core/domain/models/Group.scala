package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountGroupId, GroupId}
import io.github.cactacea.backend.core.infrastructure.models.{AccountGroups, Groups, _}

case class Group(
                  id: GroupId,
                  name: Option[String],
                  message: Option[Message],
                  groupPrivacyType: GroupPrivacyType,
                  invitationOnly: Boolean,
                  authorityType: GroupAuthorityType,
                  accountCount: Long,
                  organizedAt: Long,
                  next: Long
                 )

object Group {
  def apply(g: Groups): Group = {
    apply(g, None, None, None, None, None, None)
  }

  def apply(ag: AccountGroups, g: Groups, m: Option[Messages], am: Option[AccountMessages]): Group = {
    apply(g, m, am, None, None, None, Some(ag.id))
  }

  def apply(g: Groups, m: Option[Messages], am: Option[AccountMessages], a: Option[Accounts], r: Option[Relationships]): Group = {
    apply(g, m, am, None, a, r, None)
  }

  def apply(g: Groups,
            m: Option[Messages],
            am: Option[AccountMessages],
            a: Option[Accounts],
            r: Option[Relationships],
            accountGroupId: AccountGroupId): Group = {

    apply(g, m, am, None, a, r, Some(accountGroupId))
  }

  def apply(g: Groups,
            m: Option[Messages],
            am: Option[AccountMessages],
            i: Option[Mediums],
            a: Option[Accounts],
            r: Option[Relationships],
            accountGroupId: Option[AccountGroupId]): Group = {

    val message = (m, am, a) match {
      case (Some(m), Some(um), Some(a)) =>
        Some(Message(m, um, i, a, r))
      case _ =>
        None
    }

    Group(
      id                = g.id,
      name              = g.name,
      message           = message,
      groupPrivacyType  = g.privacyType,
      invitationOnly    = g.invitationOnly,
      authorityType     = g.authorityType,
      accountCount      = g.accountCount,
      organizedAt       = g.organizedAt,
      next              = accountGroupId.map(_.value).getOrElse(g.id.value)
    )
  }

}
