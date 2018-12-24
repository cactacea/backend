package io.github.cactacea.backend.core.domain.models

import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers.MessageId
import io.github.cactacea.backend.core.infrastructure.models._

case class GroupMessage(
                    id: MessageId,
                    messageType: MessageType,
                    message: Option[String],
                    accountCount: Long,
                    readAccountCount: Long,
                    contentWarning: Boolean,
                    contentDeleted: Boolean,
                    postedAt: Long
                  )


object GroupMessage {

  def apply(m: Messages): GroupMessage = {

    m.contentStatus match {
      case ContentStatusType.rejected =>
        GroupMessage(
          id                = m.id,
          messageType       = m.messageType,
          message           = None,
          accountCount      = 0L,
          readAccountCount  = 0L,
          contentWarning    = false,
          contentDeleted    = true,
          postedAt          = m.postedAt
        )
      case _ =>

        GroupMessage(
          id                = m.id,
          messageType       = m.messageType,
          message           = m.message,
          accountCount      = m.accountCount,
          readAccountCount  = m.readAccountCount,
          contentWarning    = m.contentWarning,
          contentDeleted    = false,
          postedAt          = m.postedAt
        )
    }
  }

}
