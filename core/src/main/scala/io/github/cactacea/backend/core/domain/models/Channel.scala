package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{ChannelAuthorityType, ChannelPrivacyType}
import io.github.cactacea.backend.core.infrastructure.identifiers.ChannelId
import io.github.cactacea.backend.core.infrastructure.models._

case class Channel(
                  id: ChannelId,
                  name: Option[String],
                  message: Option[Message],
                  invitationOnly: Boolean,
                  privacyType: ChannelPrivacyType,
                  authorityType: ChannelAuthorityType,
                  userCount: Long,
                  lastPostedAt: Option[Long],
                  organizedAt: Long,
                  next: Option[Long]
                 )

object Channel {

  def apply(g: Channels): Channel = {
    apply(g, None, None, None, None, None, None)
  }

  def apply(g: Channels, n: Long): Channel = {
    apply(g, None, None, None, None, None, Option(n))
  }

  def apply(g: Channels, am: Option[UserMessages], m: Option[Messages],
            i: Option[Mediums], a: Option[Users], r: Option[Relationships], n: Long): Channel = {
    apply(g, am, m, i, a, r, Option(n))
  }

  private def apply(g: Channels,
                    am: Option[UserMessages],
                    m: Option[Messages],
                    i: Option[Mediums],
                    a: Option[Users],
                    r: Option[Relationships], n: Option[Long]): Channel = {

    val message = ((am, m, a) match {
      case (Some(am), Some(m), Some(a)) => Option(Message(m, am, i, a, r, m.id.value))
      case _ => None
    })

    Channel(
      id                = g.id,
      name              = g.name,
      message           = message,
      invitationOnly    = g.invitationOnly,
      privacyType       = g.privacyType,
      authorityType     = g.authorityType,
      userCount         = g.userCount,
      organizedAt       = g.organizedAt,
      lastPostedAt      = g.lastPostedAt,
      next              = n
    )
  }

}
