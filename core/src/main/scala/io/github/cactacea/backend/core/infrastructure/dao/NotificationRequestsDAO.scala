package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.domain.models.{Destination, Notification}
import io.github.cactacea.backend.core.infrastructure.identifiers.FriendRequestId
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class NotificationRequestsDAO @Inject()(
                                                   db: DatabaseService,
                                                   deepLinkService: DeepLinkService) {

  import db._

  def find(id: FriendRequestId): Future[Option[Seq[Notification]]] = {
    findFriendRequest(id).flatMap(_ match {
      case Some(f) =>
        findDestinations(id).map({ d =>
          val pt = NotificationType.invitation
          val url = deepLinkService.getRequest(id)
          val r = d.groupBy(_.userName).map({ case (userName, destinations) =>
            Notification(userName, None, f.requestedAt, url, destinations, pt)
          }).toSeq
          Some(r)
        })
      case None =>
        Future.None
    })
  }

  private def findFriendRequest(id: FriendRequestId): Future[Option[FriendRequests]] = {
    val q = quote {
      query[FriendRequests]
        .filter(_.id       == lift(id))
        .filter(_.notified == false)
    }
    run(q).map(_.headOption)
  }


  private def findDestinations(id: FriendRequestId): Future[Seq[Destination]] = {
    val q = quote {
      for {
        f <- query[FriendRequests]
          .filter(_.id == lift(id))
          .filter(_.notified == lift(false))
        _ <- query[NotificationSettings]
          .join(_.userId == f.by)
          .filter(_.friendRequest == lift(true))
        a <- query[Users]
          .join(_.id == f.by)
        d <- query[Devices]
          .join(_.userId == f.by)
          .filter(_.pushToken.isDefined)
        r <- query[Relationships]
          .leftJoin(r => r.userId == f.userId && r.by == f.by)
      } yield {
        Destination(
          f.userId,
          d.pushToken.getOrElse(""),
          r.flatMap(_.displayName).getOrElse(a.displayName),
          f.by)
      }
    }
    run(q)
  }

  def update(id: FriendRequestId): Future[Unit] = {
    val q = quote {
      query[FriendRequests]
        .filter(_.id == lift(id))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }


}
