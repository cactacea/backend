package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.enums.FriendRequestStatusType
import io.github.cactacea.backend.core.domain.models.FriendRequest
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.infrastructure.models._
import io.github.cactacea.backend.core.infrastructure.results.PushNotifications
import io.github.cactacea.backend.core.util.exceptions.CactaceaException
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountAlreadyRequested, FriendRequestNotFound}

@Singleton
class FriendRequestsDAO @Inject()(db: DatabaseService) {

  import db._

  def create(accountId: AccountId, sessionId: SessionId): Future[FriendRequestId] = {
    val requestedAt = System.currentTimeMillis()
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .insert(
          _.accountId       -> lift(accountId),
          _.by              -> lift(by),
          _.notified        -> false,
          _.requestStatus   -> lift(FriendRequestStatusType.noResponded),
          _.requestedAt     -> lift(requestedAt)
        ).returning(_.id)
    }
    run(q)
  }

  def delete(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.accountId     == lift(accountId))
        .filter(_.by            == lift(by))
        .filter(_.requestStatus == lift(FriendRequestStatusType.noResponded))
        .delete
    }
    run(q).map(_ => Unit)
  }

  def exist(accountId: AccountId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.accountId   == lift(accountId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(id: FriendRequestId): Future[Option[FriendRequests]] = {
    val q = quote {
      query[FriendRequests]
        .filter(_.id          == lift(id))
    }
    run(q).map(_.headOption)
  }

  def findOwner(id: FriendRequestId, sessionId: SessionId): Future[AccountId] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.id          == lift(id))
        .filter(_.accountId   == lift(accountId))
        .map(_.by)
    }
    run(q).map(_.headOption).flatMap(_ match {
      case Some(r) =>
        Future.value(r)
      case None =>
        Future.exception(CactaceaException(FriendRequestNotFound))
    })
  }

  def find(since: Option[Long],
           offset: Int,
           count: Int,
           received: Boolean,
           sessionId: SessionId): Future[List[FriendRequest]] = {

    val by = sessionId.toAccountId

    val q = quote {
      query[FriendRequests]
        .filter(f =>
          if (lift(received)) {
            f.accountId == lift(by)
          } else {
            f.by == lift(by)
          }
        )
        .filter(f => lift(since).forall(f.id < _))
        .join(query[Accounts]).on((f, a) =>
          if (lift(received)) {
            a.id == f.by
          } else {
            a.id == f.accountId
          }
        )
        .leftJoin(query[Relationships]).on({ case ((_, a), r) => r.accountId == a.id && r.by == lift(by)})
        .map({ case ((f, a), r) => (f, a, r)})
        .sortBy({ case (f, _, _) => f.id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (f, a, r) => FriendRequest(f, a, r, f.id.value)}))

  }

  def update(id: FriendRequestId, status: FriendRequestStatusType, sessionId: SessionId): Future[Unit] = {
    val accountId = sessionId.toAccountId
    val q = quote {
      query[FriendRequests]
        .filter(_.id            == lift(id))
        .filter(_.accountId     == lift(accountId))
        .update(_.requestStatus -> lift(status))
    }
    run(q).map(_ => Unit)

  }

  // Modible Push

  def findPushNotifications(id: FriendRequestId): Future[List[PushNotifications]] = {
    val q = quote {
      query[FriendRequests].filter(g => g.id == lift(id) && g.notified == false
        && query[PushNotificationSettings].filter(p => p.accountId == g.accountId && p.friendRequest == true).nonEmpty)
        .leftJoin(query[Relationships]).on((g, r) => r.accountId == g.by && r.by == g.accountId)
        .join(query[Accounts]).on({ case ((g, _), a) => a.id == g.by})
        .join(query[Devices]).on({ case (((g, _), _), d) => d.accountId == g.accountId && d.pushToken.isDefined})
        .map({ case (((g, r), a), d) => (a.displayName, r.flatMap(_.displayName), g.accountId, d.pushToken) })
        .distinct
    }
    run(q).map(_.map({ case (displayName, editedDisplayName, accountId, pushToken) => {
      val name = editedDisplayName.getOrElse(displayName)
      val token = pushToken.get
      PushNotifications(accountId, name, token, showContent = false)
    }}))
  }

  def updatePushNotifications(id: FriendRequestId, notified: Boolean = true): Future[Unit] = {
    val q = quote {
      query[FriendRequests]
        .filter(_.id == lift(id))
        .update(_.notified -> lift(notified))
    }
    run(q).map(_ => Unit)
  }


  // Validator

  def validateExist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    exist(accountId, sessionId).flatMap(_ match {
      case false =>
        Future.exception(CactaceaException(FriendRequestNotFound))
      case true =>
        Future.Unit
    })
  }

  def validateNotExist(accountId: AccountId, sessionId: SessionId): Future[Unit] = {
    exist(accountId, sessionId).flatMap(_ match {
      case true =>
        Future.exception(CactaceaException(AccountAlreadyRequested))
      case false =>
        Future.Unit
    })
  }

}
