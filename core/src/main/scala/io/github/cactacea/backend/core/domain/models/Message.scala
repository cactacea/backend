package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{GroupId, MessageId}
import io.github.cactacea.backend.core.infrastructure.models._

case class Message(
                    id: MessageId,
                    groupId: GroupId,
                    messageType: MessageType,
                    message: Option[String],
                    medium: Option[Medium],
                    account: Account,
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
    apply(m, am, i, a, r, Some(next))
  }

  def apply(m: Messages, i: Option[Mediums], a: Accounts): Message = {
    val images = i.map(Medium(_))
    Message(
      id                = m.id,
      groupId           = m.groupId,
      messageType       = m.messageType,
      message           = m.message,
      medium            = images,
      account           = Account(a, None),
      unread            = false,
      accountCount      = m.accountCount,
      readAccountCount  = m.readAccountCount,
      contentWarning    = m.contentWarning,
      contentDeleted    = false,
      postedAt          = m.postedAt,
      next              = None
    )
  }

  // TODO : Check Relationship is required or Not ?

  def apply(m: Messages, am: AccountMessages, i: Option[Mediums], a: Accounts, r: Option[Relationships], next: Option[Long]): Message = {

    m.contentStatus match {
      case ContentStatusType.rejected =>
        Message(
          id                = m.id,
          groupId           = m.groupId,
          messageType       = m.messageType,
          message           = None,
          medium            = None,
          account           = Account(a, r),
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

        Message(
          id                = m.id,
          groupId           = m.groupId,
          messageType       = m.messageType,
          message           = m.message,
          medium            = images,
          account           = Account(a, r),
          unread            = am.unread,
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
