package io.github.cactacea.core.domain.models

import io.github.cactacea.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.core.infrastructure.identifiers.GroupId
import io.github.cactacea.core.infrastructure.models._

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
    apply(g, None, None, None, None, None)
  }

  def apply(g: Groups, m: Option[Messages], um: Option[AccountMessages], u: Option[Accounts], r: Option[Relationships]): Group = {
    apply(g, m, um, None, u, r)
  }

  def apply(g: Groups, m: Option[Messages], am: Option[AccountMessages], i: Option[Mediums], a: Option[Accounts], r: Option[Relationships]): Group = {
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
      next              = g.organizedAt
    )
  }
}