package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.domain.models.{Destination, Notification}
import io.github.cactacea.backend.core.infrastructure.identifiers.{MessageId, UserId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class NotificationMessagesDAO @Inject()(
                                             db: DatabaseService,
                                             deepLinkService: DeepLinkService) {

  import db._

  def find(id: MessageId): Future[Option[Seq[Notification]]] = {
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
        .filter(_.notified == false)
    }
    run(q).map(_.headOption)
  }

  def findDestinations(id: MessageId): Future[Seq[Destination]] = {
    val q = quote {
      for {
        am <- query[UserMessages]
          .filter(am => am.messageId == lift(id) && am.notified == false)
        g <- query[Channels]
          .join(_.id == am.channelId)
        a <- query[Users]
          .join(_.id == am.by)
        d <- query[Devices]
          .join(_.userId == am.userId)
          .filter(_.pushToken.isDefined)
        _ <- query[NotificationSettings]
          .join(_.userId == am.userId)
          .filter(p => ((p.message == true && g.directMessage == true) || (p.channelMessage == true && g.directMessage == false)))
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

  def update(messageId: MessageId): Future[Unit] = {
    val q = quote {
      query[Messages]
        .filter(_.id == lift(messageId))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }

  def update(messageId: MessageId, userIds: Seq[UserId]): Future[Unit] = {
    val q = quote {
      query[UserMessages]
        .filter(_.messageId == lift(messageId))
        .filter(m => liftQuery(userIds).contains(m.userId))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }

}
