package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.PushNotificationType
import io.github.cactacea.backend.core.domain.models.{Destination, PushNotification}
import io.github.cactacea.backend.core.infrastructure.identifiers.{AccountId, MessageId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class PushNotificationMessagesDAO @Inject()(
                                             db: DatabaseService,
                                             deepLinkService: DeepLinkService) {

  import db._

  def find(id: MessageId): Future[Option[List[PushNotification]]] = {
    findMessage(id).flatMap(_ match {
      case Some(m) =>
        findDestinations(id).map({ d =>
          val pt = PushNotificationType.invitation
          val url = deepLinkService.getMessages(m.groupId, m.id)
          val r = d.groupBy(_.accountName).map({ case (accountName, destinations) =>
            PushNotification(accountName, m.message, m.postedAt, url, destinations, pt)
          }).toList
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

  def findDestinations(id: MessageId): Future[List[Destination]] = {
    val q = quote {
      for {
        am <- query[AccountMessages]
          .filter(am => am.messageId == lift(id) && am.notified == false)
        g <- query[Groups]
          .join(_.id == am.groupId)
        a <- query[Accounts]
          .join(_.id == am.by)
        d <- query[Devices]
          .join(_.accountId == am.accountId)
          .filter(_.pushToken.isDefined)
        _ <- query[PushNotificationSettings]
          .join(_.accountId == am.accountId)
          .filter(p => ((p.message == true && g.directMessage == true) || (p.groupMessage == true && g.directMessage == false)))
        r <- query[Relationships]
          .leftJoin(r => r.accountId == am.by && r.by == am.accountId)
      } yield {
        Destination(
          am.accountId,
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

  def update(messageId: MessageId, accountIds: List[AccountId]): Future[Unit] = {
    val q = quote {
      query[AccountMessages]
        .filter(_.messageId == lift(messageId))
        .filter(m => liftQuery(accountIds).contains(m.accountId))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }

}
