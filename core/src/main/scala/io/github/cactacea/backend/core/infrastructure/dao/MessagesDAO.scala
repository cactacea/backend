package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class MessagesDAO @Inject()(db: DatabaseService) {

  import db._

  def create(channelId: ChannelId, messageType: MessageType, userCount: Long, sessionId: SessionId): Future[MessageId] = {
    val by = sessionId.userId
    val postedAt = System.currentTimeMillis()
    val mt = messageType
    val q = quote {
      query[Messages].insert(
        _.by                  -> lift(by),
        _.channelId             -> lift(channelId),
        _.messageType         -> lift(mt),
        _.userCount        -> lift(userCount),
        _.readCount           -> 0L,
        _.contentWarning      -> false,
        _.contentStatus       -> lift(ContentStatusType.unchecked),
        _.notified            -> false,
        _.postedAt            -> lift(postedAt)
      ).returning(_.id)
    }
    run(q)
  }

  def create(channelId: ChannelId, message: String, userCount: Long, sessionId: SessionId): Future[MessageId] = {
    create(channelId, Option(message), None, userCount, sessionId)
  }

  def create(channelId: ChannelId, mediumId: MediumId, userCount: Long, sessionId: SessionId): Future[MessageId] = {
    create(channelId, None, Option(mediumId), userCount, sessionId)
  }

  private def create(channelId: ChannelId, message: Option[String], mediumId: Option[MediumId], userCount: Long, sessionId: SessionId): Future[MessageId] = {
    for {
      (id, postedAt) <- insertMessages(channelId, message, userCount, mediumId, sessionId)
      _ <- updateLatestMessage(channelId, id, postedAt)
    } yield (id)
  }

  private def insertMessages(channelId: ChannelId,
                             message: Option[String],
                             userCount: Long,
                             mediumId: Option[MediumId],
                             sessionId: SessionId): Future[(MessageId, Long)] = {

    val by = sessionId.userId
    val postedAt = System.currentTimeMillis()
    val mt = if (message.isDefined) {
      MessageType.text
    } else {
      MessageType.medium
    }
    val q = quote {
      query[Messages].insert(
        _.by                  -> lift(by),
        _.channelId             -> lift(channelId),
        _.messageType         -> lift(mt),
        _.message             -> lift(message),
        _.userCount        -> lift(userCount),
        _.readCount           -> 0L,
        _.mediumId            -> lift(mediumId),
        _.contentWarning      -> false,
        _.contentStatus       -> lift(ContentStatusType.unchecked),
        _.notified            -> false,
        _.postedAt            -> lift(postedAt)
      ).returning(_.id)
    }
    run(q).map(id => (id, postedAt))
  }

  def updateReadCount(messageIds: List[MessageId]): Future[Unit] = {
    val q = quote {
      query[Messages]
        .filter(m => liftQuery(messageIds).contains(m.id))
        .update(m => m.readCount -> (m.readCount + lift(1)))
    }
    run(q).map(_ == messageIds.size)
  }

  private def updateLatestMessage(channelId: ChannelId, messageId: MessageId, postedAt: Long): Future[Unit] = {
    val messageIdOpt = Option(messageId)
    val lastPostedAtOpt = Option(postedAt)
    val q = quote {
      query[Channels]
        .filter(_.id == lift(channelId))
        .update(
          _.messageId     -> lift(messageIdOpt),
          _.lastPostedAt  -> lift(lastPostedAtOpt)

        )
    }
    run(q).map(_ => ())
  }

}
