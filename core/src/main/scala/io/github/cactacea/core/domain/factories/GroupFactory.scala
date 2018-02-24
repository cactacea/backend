package io.github.cactacea.core.domain.factories

import io.github.cactacea.core.domain.models.Group
import io.github.cactacea.core.infrastructure.models._

object GroupFactory {

  def create(g: Groups): Group = {
    create(g, None, None, None, None, None)
  }

  def create(g: Groups, m: Option[Messages], um: Option[AccountMessages], u: Option[Accounts], r: Option[Relationships]): Group = {
    create(g, m, um, None, u, r)
  }

  def create(g: Groups, m: Option[Messages], am: Option[AccountMessages], i: Option[Mediums], a: Option[Accounts], r: Option[Relationships]): Group = {
    val message = (m, am, a) match {
      case (Some(m), Some(um), Some(a)) =>
        Some(MessageFactory.create(m, um, i, a, r))
      case _ =>
        None
    }
    Group(
      id                = g.id,
      name              = g.name,
      message           = message,
      groupPrivacyType  = g.privacyType,
      byInvitationOnly  = g.byInvitationOnly,
      authorityType     = g.authorityType,
      accountCount      = g.accountCount,
      organizedAt       = g.organizedAt,
      next              = g.organizedAt
    )
  }
}
