package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class MessagesDAO @Inject()(db: DatabaseService, groupsDAO: GroupsDAO) {

  import db._

  def create(groupId: GroupId, messageType: MessageType, accountCount: Long, sessionId: SessionId): Future[MessageId] = {
    val by = sessionId.toAccountId
    val postedAt = System.currentTimeMillis()
    val mt = messageType
    val q = quote {
      query[Messages].insert(
        _.by                  -> lift(by),
        _.groupId             -> lift(groupId),
        _.messageType         -> lift(mt),
        _.accountCount        -> lift(accountCount),
        _.readCount           -> 0L,
        _.contentWarning      -> false,
        _.contentStatus       -> lift(ContentStatusType.unchecked),
        _.notified            -> false,
        _.postedAt            -> lift(postedAt)
      ).returning(_.id)
    }
    run(q)
  }

  def create(groupId: GroupId, message: String, accountCount: Long, sessionId: SessionId): Future[MessageId] = {
    create(groupId, Option(message), None, accountCount, sessionId)
  }

  def create(groupId: GroupId, mediumId: MediumId, accountCount: Long, sessionId: SessionId): Future[MessageId] = {
    create(groupId, None, Option(mediumId), accountCount, sessionId)
  }

  private def create(groupId: GroupId, message: Option[String], mediumId: Option[MediumId], accountCount: Long, sessionId: SessionId): Future[MessageId] = {
    for {
      (id, postedAt) <- insertMessages(groupId, message, accountCount, mediumId, sessionId)
      _ <- updateLatestMessage(groupId, id, postedAt)
    } yield (id)
  }

  private def insertMessages(groupId: GroupId,
                     message: Option[String],
                     accountCount: Long,
                     mediumId: Option[MediumId],
                     sessionId: SessionId): Future[(MessageId, Long)] = {

    val by = sessionId.toAccountId
    val postedAt = System.currentTimeMillis()
    val mt = if (message.isDefined) {
      MessageType.text
    } else {
      MessageType.medium
    }
    val q = quote {
      query[Messages].insert(
        _.by                  -> lift(by),
        _.groupId             -> lift(groupId),
        _.messageType         -> lift(mt),
        _.message             -> lift(message),
        _.accountCount        -> lift(accountCount),
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

  private def updateLatestMessage(groupId: GroupId, messageId: MessageId, postedAt: Long): Future[Unit] = {
    val messageIdOpt = Option(messageId)
    val lastPostedAtOpt = Option(postedAt)
    val q = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .update(
          _.messageId     -> lift(messageIdOpt),
          _.lastPostedAt  -> lift(lastPostedAtOpt)

        )
    }
    run(q).map(_ => ())
  }

}
