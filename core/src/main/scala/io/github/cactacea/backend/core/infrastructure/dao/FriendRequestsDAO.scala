package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.interfaces.DeepLinkService
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.NotificationType
import io.github.cactacea.backend.core.domain.models.{Destination, FriendRequest, Notification}
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FriendRequestsDAO @Inject()(db: DatabaseService, relationshipsDAO: RelationshipsDAO, deepLinkService: DeepLinkService) {

  import db._

  def create(userId: UserId, sessionId: SessionId): Future[FriendRequestId] = {
    for {
      _ <- relationshipsDAO.createRequestInProgress(userId, sessionId)
      r <- createFriendsRequest(userId, sessionId)
    } yield (r)
  }

  private def createFriendsRequest(userId: UserId, sessionId: SessionId): Future[FriendRequestId] = {
    val requestedAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[FriendRequests]
        .insert(
          _.userId       -> lift(userId),
          _.by              -> lift(by),
          _.notified        -> false,
          _.requestedAt     -> lift(requestedAt)
        ).returningGenerated(_.id)
    }
    run(q)
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- relationshipsDAO.deleteRequestInProgress(userId, sessionId)
      r <- deleteFriendRequests(userId, sessionId)
    } yield (r)
  }

  private def deleteFriendRequests(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[FriendRequests]
        .filter(_.userId     == lift(userId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(userId: UserId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[FriendRequests]
        .filter(_.userId   == lift(userId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(id: FriendRequestId, userId: UserId): Future[Option[UserId]] = {
    val q = quote {
      query[FriendRequests]
        .filter(_.id          == lift(id))
        .filter(_.userId   == lift(userId))
        .map(_.by)
    }
    run(q).map(_.headOption)
  }

  def find(since: Option[Long],
           offset: Int,
           count: Int,
           received: Boolean,
           sessionId: SessionId): Future[Seq[FriendRequest]] = {


    if (received) {
      findReceivedRequests(since, offset, count, sessionId)
    } else {
      findSentRequests(since, offset, count, sessionId)
    }

  }

  private def findReceivedRequests(since: Option[Long],
                                   offset: Int,
                                   count: Int,
                                   sessionId: SessionId): Future[Seq[FriendRequest]] = {

    val by = sessionId.userId

    val q = quote {
      (for {
        f <- query[FriendRequests]
          .filter(_.userId == lift(by))
          .filter(f => lift(since).forall(f.id < _))
        a <- query[Users]
          .filter(_.id == f.by)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (f, a, r))
        .sortBy({ case (f, _, _) => f.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (f, a, r) => FriendRequest(f, a, r, f.id.value)}))

  }

  private def findSentRequests(since: Option[Long],
                               offset: Int,
                               count: Int,
                               sessionId: SessionId): Future[Seq[FriendRequest]] = {

    val by = sessionId.userId

    val q = quote {
      (for {
        f <- query[FriendRequests]
          .filter(_.by == lift(by))
          .filter(f => lift(since).forall(f.id < _))
        a <- query[Users]
          .filter(_.id == f.userId)
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (f, a, r))
        .sortBy({ case (f, _, _) => f.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (f, a, r) => FriendRequest(f, a, r, f.id.value)}))

  }


  // Notifications

  def findNotifications(id: FriendRequestId): Future[Option[Seq[Notification]]] = {
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
        .filter(!_.notified)
    }
    run(q).map(_.headOption)
  }


  private def findDestinations(id: FriendRequestId): Future[Seq[Destination]] = {
    val q = quote {
      for {
        f <- query[FriendRequests]
          .filter(_.id == lift(id))
          .filter(!_.notified)
        _ <- query[NotificationSettings]
          .join(_.userId == f.by)
          .filter(_.friendRequest)
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

  def updateNotified(id: FriendRequestId): Future[Unit] = {
    val q = quote {
      query[FriendRequests]
        .filter(_.id == lift(id))
        .update(_.notified -> true)
    }
    run(q).map(_ => ())
  }

}
