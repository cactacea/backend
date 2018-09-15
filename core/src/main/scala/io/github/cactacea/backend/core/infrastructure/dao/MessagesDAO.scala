package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.application.services.TimeService
import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class MessagesDAO @Inject()(db: DatabaseService) {

  import db._

  @Inject private var timeService: TimeService = _

  def create(groupId: GroupId, accountCount: Long, accountId: AccountId, messageType: MessageType, sessionId: SessionId): Future[MessageId] = {
    val by = sessionId.toAccountId
    val postedAt = timeService.currentTimeMillis()
    val mt = messageType
    val q = quote {
      query[Messages].insert(
        _.by                  -> lift(by),
        _.groupId             -> lift(groupId),
        _.messageType         -> lift(mt),
        _.accountCount        -> lift(accountCount),
        _.readAccountCount    -> 0L,
        _.contentWarning      -> false,
        _.contentStatus       -> lift(ContentStatusType.unchecked),
        _.notified            -> false,
        _.postedAt            -> lift(postedAt)
      ).returning(_.id)
    }
    run(q)
  }

  def create(groupId: GroupId, message: Option[String], accountCount: Long, mediumId: Option[MediumId], sessionId: SessionId): Future[MessageId] = {
    val by = sessionId.toAccountId
    val postedAt = timeService.currentTimeMillis()
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
        _.readAccountCount    -> 0L,
        _.mediumId            -> lift(mediumId),
        _.contentWarning      -> false,
        _.contentStatus       -> lift(ContentStatusType.unchecked),
        _.notified            -> false,
        _.postedAt            -> lift(postedAt)
      ).returning(_.id)
    }
    run(q)
  }

  def delete(groupId: GroupId): Future[Unit] = {
    val r = quote {
      query[Messages]
        .filter(_.groupId == lift(groupId))
        .delete
    }
    run(r).map(_ => Unit)
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

  def updateReadAccountCount(messageIds: List[MessageId]): Future[Unit] = {
    val q = quote {
      query[Messages]
        .filter(m => liftQuery(messageIds).contains(m.id))
        .update(m => m.readAccountCount -> (m.readAccountCount + lift(1)))
    }
    run(q).map(_ == messageIds.size)
  }

  def updateNotified(messageId: MessageId): Future[Unit] = {
    val q = quote {
      query[Messages]
        .filter(_.id == lift(messageId))
        .update(_.notified -> true)
    }
    run(q).map(_ => Unit)
  }

}
