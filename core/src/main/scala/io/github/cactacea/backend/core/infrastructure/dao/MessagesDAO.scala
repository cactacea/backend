package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._
import io.github.cactacea.backend.core.infrastructure.results.PushNotifications

@Singleton
class MessagesDAO @Inject()(db: DatabaseService) {

  import db._

  def create(groupId: GroupId, messageType: MessageType, sessionId: SessionId): Future[MessageId] = {
    for {
      c <- findAccountCount(groupId)
      id <- insert(groupId, c, messageType, sessionId)
    } yield (id)
  }

  private def insert(groupId: GroupId, accountCount: Long, messageType: MessageType, sessionId: SessionId): Future[MessageId] = {
    val by = sessionId.toAccountId
    val postedAt = System.currentTimeMillis()
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

  def create(groupId: GroupId, message: Option[String], mediumId: Option[MediumId], sessionId: SessionId): Future[MessageId] = {
    for {
      c <- findAccountCount(groupId)
      (id, postedAt) <- insert(groupId, message, c, mediumId, sessionId)
      _ <- updateLatestMessage(groupId, id, postedAt)
    } yield (id)
  }

  private def findAccountCount(groupId: GroupId): Future[Long] = {
    val q = quote {
      query[AccountGroups]
        .filter(_.groupId == lift(groupId))
        .size
    }
    run(q)
  }

  private def updateLatestMessage(groupId: GroupId, messageId: MessageId, postedAt: Long): Future[Unit] = {
    val messageIdOpt: Option[MessageId] = Some(messageId)
    val lastPostedAtOpt: Option[Long] = Some(postedAt)
    val r = quote {
      query[Groups]
        .filter(_.id == lift(groupId))
        .update(
          _.messageId     -> lift(messageIdOpt),
          _.lastPostedAt  -> lift(lastPostedAtOpt)

        )
    }
    run(r).map(_ => Unit)
  }


  private def insert(groupId: GroupId,
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
        _.readAccountCount    -> 0L,
        _.mediumId            -> lift(mediumId),
        _.contentWarning      -> false,
        _.contentStatus       -> lift(ContentStatusType.unchecked),
        _.notified            -> false,
        _.postedAt            -> lift(postedAt)
      ).returning(_.id)
    }
    run(q).map(id => (id, postedAt))
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
      query[Messages]
        .filter(_.id == lift(messageId))
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


  def findPushNotifications(id: MessageId): Future[List[PushNotifications]] = {
    val q = quote {
      query[AccountMessages]
        .filter(am => am.messageId == lift(id) && am.notified == false)
        .filter(am => query[Relationships].filter(r => r.accountId == am.by && r.by == am.accountId && r.muting == true).isEmpty)
        .join(query[Groups]).on((am, g) => g.id == am.groupId)
        .join(query[PushNotificationSettings]).on({ case ((am, g), p) => p.accountId == am.accountId &&
        ((p.message == true && g.directMessage == true) || (p.groupMessage == true && g.directMessage == false))})
        .leftJoin(query[Relationships]).on({ case (((am, _), _), r) =>  r.accountId == am.by && r.by == am.accountId })
        .join(query[Accounts]).on({ case ((((am, _), _), _), a) =>  a.id == am.by})
        .join(query[Devices]).on({ case (((((am, _), _), _), _), d) => d.accountId == am.accountId && d.pushToken.isDefined})
        .map({case (((((am, _), p), r), a), d) => (a.displayName, r.flatMap(_.displayName), p.showMessage, am.accountId, d.pushToken) })
        .distinct
    }
    run(q).map(_.map({ case (displayName, editedDisplayName, showMessage, accountId, pushToken) => {
      val name = editedDisplayName.getOrElse(displayName)
      val token = pushToken.get
      PushNotifications(accountId, name, token, showMessage)
    }}))

  }

  def updatePushNotifications(messageId: MessageId): Future[Unit] = {
    val q = quote {
      query[Messages]
        .filter(_.id == lift(messageId))
        .update(_.notified -> true)
    }
    run(q).map(_ => Unit)
  }

}
