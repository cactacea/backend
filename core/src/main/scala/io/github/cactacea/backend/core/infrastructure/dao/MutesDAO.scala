package io.github.cactacea.backend.core.infrastructure.dao

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.domain.models.User
import io.github.cactacea.backend.core.infrastructure.identifiers.{UserId, SessionId}
import io.github.cactacea.backend.core.infrastructure.models.{Users, Mutes, Relationships}

@Singleton
class MutesDAO @Inject()(db: DatabaseService, relationshipsDAO: RelationshipsDAO) {

  import db._

  def create(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- createMutes(userId, sessionId)
      _ <- relationshipsDAO.createMute(userId, sessionId)
    } yield (r)
  }

  private def createMutes(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val mutedAt = System.currentTimeMillis()
    val by = sessionId.userId
    val q = quote {
      query[Mutes]
        .insert(
          _.userId       -> lift(userId),
          _.by              -> lift(by),
          _.mutedAt         -> lift(mutedAt)
        )
    }
    run(q).map(_ => ())
  }

  def delete(userId: UserId, sessionId: SessionId): Future[Unit] = {
    for {
      r <- deleteMutes(userId, sessionId)
      _ <- relationshipsDAO.deleteMute(userId, sessionId)
    } yield (r)
  }

  private def deleteMutes(userId: UserId, sessionId: SessionId): Future[Unit] = {
    val by = sessionId.userId
    val q = quote {
      query[Mutes]
        .filter(_.userId   == lift(userId))
        .filter(_.by          == lift(by))
        .delete
    }
    run(q).map(_ => ())
  }

  def own(userId: UserId, sessionId: SessionId): Future[Boolean] = {
    val by = sessionId.userId
    val q = quote {
      query[Mutes]
        .filter(_.userId == lift(userId))
        .filter(_.by        == lift(by))
        .nonEmpty
    }
    run(q)
  }

  def find(userName: Option[String], since: Option[Long], offset: Int, count: Int, sessionId: SessionId): Future[List[User]] = {
    val by = sessionId.userId
    val q = quote {
      (for {
        m <- query[Mutes]
          .filter(m => m.by == lift(by))
          .filter(m => lift(since).forall(m.id < _))
        a <- query[Users]
            .join(_.id == m.userId)
            .filter(a => lift(userName.map(_ + "%")).forall(a.userName like _))
        r <- query[Relationships]
            .leftJoin(r => r.userId == a.id && r.by == lift(by))
      } yield (a, r, m.id))
        .sortBy({ case (_, _, id) => id})(Ord.desc)
        .drop(lift(offset))
        .take(lift(count))
    }
    run(q).map(_.map({case (a, r, id) => User(a, r, id.value)}))
  }


}
