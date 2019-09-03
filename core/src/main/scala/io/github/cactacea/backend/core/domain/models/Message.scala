package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{UserStatusType, ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers.{ChannelId, MessageId}
import io.github.cactacea.backend.core.infrastructure.models._

case class Message(
                    id: MessageId,
                    channelId: ChannelId,
                    messageType: MessageType,
                    message: Option[String],
                    medium: Option[Medium],
                    user: User,
                    unread: Boolean,
                    userCount: Long,
                    readUserCount: Long,
                    warning: Boolean,
                    rejected: Boolean,
                    postedAt: Long,
                    next: Long
                  )

object Message {

  def apply(m: Messages, am: UserMessages, i: Option[Mediums], a: Users, r: Option[Relationships], next: Long): Message = {
    val rejected = (m.contentStatus == ContentStatusType.rejected) || (a.userStatus != UserStatusType.normally)
    rejected match {
      case true =>
        Message(
          id                = m.id,
          channelId           = m.channelId,
          messageType       = m.messageType,
          message           = None,
          medium            = None,
          user           = User(a, r),
          unread            = false,
          userCount      = 0L,
          readUserCount  = 0L,
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
          user           = User(a, r),
          unread            = am.unread,
          userCount      = m.userCount,
          readUserCount  = m.readCount,
          warning           = m.contentWarning,
          rejected          = rejected,
          postedAt          = m.postedAt,
          next              = next
        )
    }

  }

}
