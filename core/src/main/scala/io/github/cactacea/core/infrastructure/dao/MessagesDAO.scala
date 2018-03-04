package io.github.cactacea.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.MessageType
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.infrastructure.models._
import io.github.cactacea.core.infrastructure.services.DatabaseService

@Singleton
class MessagesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject var identifiesDAO: IdentifiesDAO = _

  def create(groupId: GroupId, accountCount: Long, accountId: AccountId, messageType: MessageType, sessionId: SessionId): Future[MessageId] = {
    for {
      id <- identifiesDAO.create().map(MessageId(_))
      _ <- insert(id, groupId, accountCount, accountId, messageType, sessionId)
    } yield (id)
  }

  private def insert(id: MessageId, groupId: GroupId, accountCount: Long, accountId: AccountId, messageType: MessageType, sessionId: SessionId): Future[Long] = {
    val by = sessionId.toAccountId
    val postedAt = System.nanoTime()
    val mt = messageType.toValue
    val q = quote {
      query[Messages].insert(
        _.id                  -> lift(id),
        _.by                  -> lift(by),
        _.groupId             -> lift(groupId),
        _.messageType         -> lift(mt),
        _.accountCount        -> lift(accountCount),
        _.readAccountCount    -> lift(0L),
        _.notified            -> lift(true),
        _.postedAt            -> lift(postedAt)
      )
    }
    run(q)
  }

  def create(groupId: GroupId, message: Option[String], accountCount: Long, mediumId: Option[MediumId], sessionId: SessionId): Future[MessageId] = {
    for {
      id <- identifiesDAO.create().map(MessageId(_))
      _ <- insert(id, groupId, message, accountCount, mediumId, sessionId)
    } yield (id)
  }

  private def insert(id: MessageId, groupId: GroupId, message: Option[String], accountCount: Long, mediumId: Option[MediumId], sessionId: SessionId): Future[Long] = {
    val by = sessionId.toAccountId
    val postedAt = System.nanoTime()
    val mt = if (message.isDefined) {
      MessageType.text.toValue
    } else {
      MessageType.medium.toValue
    }
    val q = quote {
      query[Messages].insert(
        _.id                  -> lift(id),
        _.by                  -> lift(by),
        _.groupId             -> lift(groupId),
        _.messageType         -> lift(mt),
        _.message             -> lift(message),
        _.accountCount        -> lift(accountCount),
        _.readAccountCount    -> lift(0L),
        _.mediumId            -> lift(mediumId),
        _.notified            -> lift(false),
        _.postedAt            -> lift(postedAt)
      )
    }
    run(q)
  }

  def delete(groupId: GroupId): Future[Boolean] = {
    val r = quote {
      query[Messages]
        .filter(_.groupId == lift(groupId))
        .delete
    }
    run(r).map(_ >= 1)
  }

  def find(messageId: MessageId): Future[Option[Messages]] = {
    val q = quote {
      for {
        m <- query[Messages]
          .filter(_.id == lift(messageId))
      } yield (m)
    }
    run(q).map(_.headOption)
  }

  def updateReadAccountCount(messageIds: List[MessageId]): Future[Boolean] = {
    val q = quote {
      query[Messages]
        .filter(m => liftQuery(messageIds).contains(m.id))
        .update(m => m.readAccountCount -> (m.readAccountCount + lift(1)))
    }
    run(q).map(_ == messageIds.size)
  }

  // TODO
  def updateNotified(messageId: MessageId): Future[Boolean] = {
    val q = quote {
      query[Messages]
        .filter(_.id == lift(messageId))
        .update(_.notified -> lift(true))
    }
    run(q).map(_ == 1)
  }

}
