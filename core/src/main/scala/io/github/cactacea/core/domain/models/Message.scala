package io.github.cactacea.core.domain.models

import io.github.cactacea.core.infrastructure.identifiers.MessageId
import io.github.cactacea.core.infrastructure.models._

case class Message(
                    id: MessageId,
                    messageType: Long,
                    message: Option[String],
                    medium: Option[Medium],
                    by: Account,
                    account: Option[Account],
                    unread: Boolean,
                    accountCount: Long,
                    readAccountCount: Long,
                    postedAt: Long
                  )

object Message {

  def apply(m: Messages, am: AccountMessages, i: Option[Mediums], a: Accounts, r: Option[Relationships]): Message = {
    apply(m, am, i, a, r, None, None)
  }

  def apply(m: Messages, am: AccountMessages, i: Option[Mediums], a: Accounts, r: Option[Relationships], a2: Option[Accounts], r2: Option[Relationships]): Message = {
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
      postedAt          = m.postedAt
    )
  }


}