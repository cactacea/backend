package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.PushNotificationType
import io.github.cactacea.backend.core.domain.models.{Destination, PushNotification}
import io.github.cactacea.backend.core.infrastructure.identifiers.InvitationId
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class PushNotificationInvitationsDAO @Inject()(
                                                     db: DatabaseService,
                                                     deepLinkService: DeepLinkService) {

  import db._

  def find(id: InvitationId): Future[Option[List[PushNotification]]] = {
    findInvitation(id).flatMap(_.map(f => f) match {
      case Some(f) =>
        findDestinations(id).map({ d =>
          val pt = PushNotificationType.invitation
          val url = deepLinkService.getInvitation(id)
          val r = d.groupBy(_.userName).map({ case (userName, destinations) =>
            PushNotification(userName, None, f.invitedAt, url, destinations, pt)
          }).toList
          Option(r)
        })
      case None =>
        Future.None
    })
  }


  private def findInvitation(id: InvitationId): Future[Option[Invitations]] = {
    val q = quote {
      query[Invitations]
        .filter(_.id == lift(id))
    }
    run(q).map(_.headOption)
  }

  def findDestinations(id: InvitationId): Future[List[Destination]] = {
    val q = quote {
      for {
        g <- query[Invitations]
          .filter(_.id == lift(id))
          .filter(_.notified == lift(false))
        a <- query[Users]
          .join(_.id == g.by)
        d <- query[Devices]
          .join(_.userId == g.userId)
          .filter(_.pushToken.isDefined)
        _ <- query[PushNotificationSettings]
          .join(_.userId == g.userId)
          .filter(_.invitation == lift(true))
        r <- query[Relationships]
          .leftJoin(r => r.userId == g.by && r.by == g.userId)
      } yield {
        Destination(
          g.userId,
          d.pushToken.getOrElse(""),
          r.flatMap(_.displayName).getOrElse(a.displayName),
          g.by)
      }
    }
    run(q)
  }

  def update(id: InvitationId): Future[Unit] = {
    val q = quote {
      query[Invitations]
        .filter(_.id == lift(id))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }


}
