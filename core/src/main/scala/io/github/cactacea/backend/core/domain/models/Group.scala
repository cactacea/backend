package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{GroupAuthorityType, GroupPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupId
import io.github.cactacea.backend.core.infrastructure.models.{Groups, _}

case class Group(
                  id: GroupId,
                  name: Option[String],
                  message: Option[GroupMessage],
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
    apply(g, None, None)
  }

  def apply(g: Groups, n: Long): Group = {
    apply(g, None, Some(n))
  }

  def apply(g: Groups, m: Option[Messages], n: Long): Group = {
    apply(g, m, Some(n))
  }

  def apply(g: Groups,
            m: Option[Messages],
            n: Option[Long]): Group = {

    val message = m.map(GroupMessage(_))

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
