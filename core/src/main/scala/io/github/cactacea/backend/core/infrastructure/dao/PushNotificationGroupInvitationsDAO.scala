package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.PushNotificationType
import io.github.cactacea.backend.core.domain.models.{Destination, PushNotification}
import io.github.cactacea.backend.core.infrastructure.identifiers.GroupInvitationId
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class PushNotificationGroupInvitationsDAO @Inject()(
                                                     db: DatabaseService,
                                                     deepLinkService: DeepLinkService) {

  import db._

  def find(id: GroupInvitationId): Future[Option[List[PushNotification]]] = {
    findGroupInvitation(id).flatMap(_ match {
      case Some(f) =>
        findDestinations(id).map({ d =>
          val pt = PushNotificationType.groupInvitation
          val url = deepLinkService.getInvitation(id)
          val r = d.groupBy(_.accountName).map({ case (accountName, destinations) =>
            PushNotification(accountName, f.invitedAt, url, destinations, pt)
          }).toList
          Some(r)
        })
      case None =>
        Future.None
    })
  }


  private def findGroupInvitation(id: GroupInvitationId): Future[Option[GroupInvitations]] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id == lift(id))
    }
    run(q).map(_.headOption)
  }

  def findDestinations(id: GroupInvitationId): Future[List[Destination]] = {
    val q = quote {
      for {
        g <- query[GroupInvitations]
          .filter(_.id == lift(id))
          .filter(_.notified == lift(false))
        a <- query[Accounts]
          .join(_.id == g.accountId)
        d <- query[Devices]
          .join(_.accountId == g.accountId)
          .filter(_.pushToken.isDefined)
        _ <- query[PushNotificationSettings]
          .join(_.accountId == g.accountId)
          .filter(_.groupInvitation == lift(true))
        r <- query[Relationships]
          .leftJoin(r => r.accountId == g.by && r.by == g.accountId)
      } yield {
        Destination(
          g.accountId,
          d.pushToken.getOrElse(""),
          r.flatMap(_.displayName).getOrElse(a.displayName),
          g.by)
      }
    }
    run(q)
  }

  def update(id: GroupInvitationId): Future[Unit] = {
    val q = quote {
      query[GroupInvitations]
        .filter(_.id == lift(id))
        .update(_.notified -> true)
    }
    run(q).map(_ => Unit)
  }


}
