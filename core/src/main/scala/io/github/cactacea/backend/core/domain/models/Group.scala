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
    apply(g, None, None, None, None, None, None)
  }

  def apply(g: Groups, n: Long): Group = {
    apply(g, None, None, None, None, None, Option(n))
  }

  def apply(g: Groups, am: Option[AccountMessages], m: Option[Messages], i: Option[Mediums], a: Option[Accounts], r: Option[Relationships], n: Long): Group = {
    apply(g, am, m, i, a, r, Option(n))
  }

  private def apply(g: Groups,
                    am: Option[AccountMessages],
                    m: Option[Messages],
                    i: Option[Mediums],
                    a: Option[Accounts],
                    r: Option[Relationships], n: Option[Long]): Group = {

    val message = ((am, m, a) match {
      case (Some(am), Some(m), Some(a)) => Option(Message(m, am, i, a, r, m.id.value))
      case _ => None
    })

    Group(
      id                = g.id,
      name              = g.name,
      message           = message,
      invitationOnly    = g.invitationOnly,
      privacyType       = g.privacyType,
      authorityType     = g.authorityType,
      accountCount      = g.accountCount,
      organizedAt       = g.organizedAt,
      lastPostedAt      = g.lastPostedAt,
      next              = n
    )
  }

}
