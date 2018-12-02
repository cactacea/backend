package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{MessageId}
import io.github.cactacea.backend.core.infrastructure.models._

case class Message(
                    id: MessageId,
                    messageType: MessageType,
                    message: Option[String],
                    medium: Option[Medium],
                    account: Option[Account],
                    unread: Boolean,
                    accountCount: Long,
                    readAccountCount: Long,
                    contentWarning: Boolean,
                    contentDeleted: Boolean,
                    postedAt: Long,
                    next: Option[Long]
                  )

object Message {

  def apply(m: Messages, am: AccountMessages, i: Option[Mediums], a: Accounts, r: Option[Relationships], next: Long): Message = {
    apply(m, Some(am), i, Some(a), r, Some(next))
  }

  def apply(m: Messages, am: Option[AccountMessages]): Message = {
    apply(m, am, None, None, None, None)
  }

  def apply(m: Messages, am: Option[AccountMessages], i: Option[Mediums], a: Option[Accounts], r: Option[Relationships], next: Option[Long]): Message = {

    m.contentStatus match {
      case ContentStatusType.rejected =>
        Message(
          id                = m.id,
          messageType       = m.messageType,
          message           = None,
          medium            = None,
          account           = None,
          unread            = false,
          accountCount      = 0L,
          readAccountCount  = 0L,
          contentWarning    = false,
          contentDeleted    = true,
          postedAt          = m.postedAt,
          next              = None
        )
      case _ =>
        val images = i.map(Medium(_))
        val account: Option[Account] = a.map(Account(_, r))

        Message(
          id                = m.id,
          messageType       = m.messageType,
          message           = m.message,
          medium            = images,
          account           = account,
          unread            = am.map(_.unread).getOrElse(false),
          accountCount      = m.accountCount,
          readAccountCount  = m.readAccountCount,
          contentWarning    = m.contentWarning,
          contentDeleted    = false,
          postedAt          = m.postedAt,
          next              = next
        )
    }
  }

}
