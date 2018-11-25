package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers.MessageId
import io.github.cactacea.backend.core.infrastructure.models._

case class Message(
                    id: MessageId,
                    messageType: MessageType,
                    message: Option[String],
                    medium: Option[Medium],
                    by: Account,
                    account: Option[Account],
                    unread: Boolean,
                    accountCount: Long,
                    readAccountCount: Long,
                    contentWarning: Boolean,
                    contentDeleted: Boolean,
                    postedAt: Long
                  )

object Message {

  def apply(m: Messages, am: AccountMessages, i: Option[Mediums], a: Accounts, r: Option[Relationships]): Message = {

    val a2: Option[Accounts] = None
    val r2: Option[Relationships] = None

    m.contentStatus match {
      case ContentStatusType.rejected =>
        val by = Account(a, r)
        val account = a2.map(Account(_, r2))
        Message(
          id                = m.id,
          messageType       = m.messageType,
          message           = None,
          medium            = None,
          by                = by,
          account           = account,
          unread            = false,
          accountCount      = 0L,
          readAccountCount  = 0L,
          contentWarning    = false,
          contentDeleted   = true,
          postedAt          = m.postedAt
        )
      case _ =>
        val images = i.map(Medium(_))
        val by = Account(a, r)
        val account = a2.map(Account(_, r2))
        Message(
          id                = m.id,
          messageType       = m.messageType,
          message           = m.message,
          medium            = images,
          by                = by,
          account           = account,
          unread            = am.unread,
          accountCount      = m.accountCount,
          readAccountCount  = m.readAccountCount,
          contentWarning    = m.contentWarning,
          contentDeleted   = false,
          postedAt          = m.postedAt
        )
    }
  }

}
