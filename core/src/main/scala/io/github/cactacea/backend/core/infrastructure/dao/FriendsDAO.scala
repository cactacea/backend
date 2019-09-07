package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models._

@Singleton
class FriendsDAO @Inject()(db: DatabaseService, relationshipsDAO: RelationshipsDAO) {

  import db._

  def create(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- createFriends(userId, sessionId)
      _ <- updateFriendCount(1L, sessionId)
      _ <- relationshipsDAO.updateFriendBlockCount(userId, 1L, sessionId)
      _ <- relationshipsDAO.createFriend(userId, sessionId)
    } yield (())
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      _ <- deleteFriends(userId, sessionId)
      _ <- updateFriendCount(-1L, sessionId)
      _ <- relationshipsDAO.updateFriendBlockCount(userId, -1L, sessionId)
      _ <- relationshipsDAO.deleteFriend(userId, sessionId)
    } yield (())
  }

  private def createFriends(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val friendsAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[Friends]
        .insert(
          _.userId       -> lift(userId),
          _.by              -> lift(by),
          _.friendedAt      -> lift(friendsAt)
        )
    }
    run(q).map(_ => ())
  }

  private def deleteFriends(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Friends]
        .filter(_.userId     == lift(userId))
        .filter(_.by            == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(userId: UserId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Friends]
        .filter(_.userId   == lift(userId))
        .filter(_.by          == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(userName: Option[String],
           since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId): Future[Seq[User]] = {
    find(sessionId.userId, userName, since, offset, count, sessionId)
  }

  def find(userId: UserId,
           userName: Option[String],
           since: Option[Long],
           offset: Int,
           count: Int,
           sessionId: SessionId): Future[Seq[User]] = {

    val by = sessionId.userId

    val q = quote {
      (for {
        f <- query[Friends]
          .filter(f => f.by == lift(userId))
          .filter(f => lift(since).forall(f.id < _))
          .filter(f => query[Blocks].filter(b => b.userId == lift(by) && b.by == f.userId).isEmpty)
        a <- query[Users]
          .join(_.id == f.userId)
          .filter(a => lift(userName.map(_ + "%")).forall(a.userName like _))
        r <- query[Relationships]
          .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (a, r, f.id))
        .sortBy({ case (_, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => User(a, r, id.value)}))

  }

  private def updateFriendCount(plus: Long, sessionId: SessionId): Future[Unit] = {
    val userId = sessionId.userId
    val q = quote {
      query[Users]
        .filter(_.id == lift(userId))
        .update(
          a => a.friendCount -> (a.friendCount + lift(plus))
        )
    }
    run(q).map(_ => ())
  }



}
