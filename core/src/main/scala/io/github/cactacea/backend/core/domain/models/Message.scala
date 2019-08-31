package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{AccountStatusType, ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, MessageId}
import io.github.cactacea.backend.core.infrastructure.models._

case class Message(
                    id: MessageId,
                    channelId: ChannelId,
                    messageType: MessageType,
                    message: Option[String],
                    medium: Option[Medium],
                    account: Account,
                    unread: Boolean,
                    accountCount: Long,
                    readAccountCount: Long,
                    warning: Boolean,
                    rejected: Boolean,
                    postedAt: Long,
                    next: Long
                  )

object Message {

  def apply(m: Messages, am: AccountMessages, i: Option[Mediums], a: Accounts, r: Option[Relationships], next: Long): Message = {
    val rejected = (m.contentStatus == ContentStatusType.rejected) || (a.accountStatus != AccountStatusType.normally)
    rejected match {
      case true =>
        Message(
          id                = m.id,
          channelId           = m.channelId,
          messageType       = m.messageType,
          message           = None,
          medium            = None,
          account           = Account(a, r),
          unread            = false,
          accountCount      = 0L,
          readAccountCount  = 0L,
          warning           = false,
          rejected          = rejected,
          postedAt          = m.postedAt,
          next              = next
        )
      case false =>
        Message(
          id                = m.id,
          channelId           = m.channelId,
          messageType       = m.messageType,
          message           = m.message,
          medium            = i.map(Medium(_)),
          account           = Account(a, r),
          unread            = am.unread,
          accountCount      = m.accountCount,
          readAccountCount  = m.readCount,
          warning           = m.contentWarning,
          rejected          = rejected,
          postedAt          = m.postedAt,
          next              = next
        )
    }

  }

}
