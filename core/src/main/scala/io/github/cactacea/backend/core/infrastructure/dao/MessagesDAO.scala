package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.{ContentStatusType, MessageType, NotificationType}
import io.github.cactacea.backend.core.domain.models.{Destination, Notification}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class MessagesDAO @Inject()(db: DatabaseService, deepLinkService: DeepLinkService) {

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

  def updateReadCount(messageIds: Seq[MessageId]): Future[Unit] = {
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



  // notifications

  def findNotifications(id: MessageId): Future[Option[Seq[Notification]]] = {
    findMessage(id).flatMap(_ match {
      case Some(m) =>
        findDestinations(id).map({ d =>
          val pt = NotificationType.invitation
          val url = deepLinkService.getMessages(m.channelId, m.id)
          val r = d.groupBy(_.userName).map({ case (userName, destinations) =>
            Notification(userName, m.message, m.postedAt, url, destinations, pt)
          }).toSeq
          Some(r)
        })
      case None =>
        Future.None
    })
  }

  private def findMessage(id: MessageId): Future[Option[Messages]] = {
    val q = quote {
      query[Messages]
        .filter(_.id == lift(id))
        .filter(!_.notified)
    }
    run(q).map(_.headOption)
  }

  private def findDestinations(id: MessageId): Future[Seq[Destination]] = {
    val q = quote {
      for {
        am <- query[UserMessages]
          .filter(am => am.messageId == lift(id) && !am.notified)
        g <- query[Channels]
          .join(_.id == am.channelId)
        a <- query[Users]
          .join(_.id == am.by)
        d <- query[Devices]
          .join(_.userId == am.userId)
          .filter(_.pushToken.isDefined)
        _ <- query[NotificationSettings]
          .join(_.userId == am.userId)
          .filter(p => ((p.message && g.directMessage) || (p.channelMessage && !g.directMessage)))
        r <- query[Relationships]
          .leftJoin(r => r.userId == am.by && r.by == am.userId)
      } yield {
        Destination(
          am.userId,
          d.pushToken.getOrElse(""),
          r.flatMap(_.displayName).getOrElse(a.displayName),
          am.by)
      }
    }
    run(q)
  }

  def updateNotified(messageId: MessageId): Future[Unit] = {
    val q = quote {
      query[Messages]
        .filter(_.id == lift(messageId))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }

}
